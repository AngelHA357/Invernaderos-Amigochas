package org.itson.Anomalyzer;

import jakarta.annotation.PostConstruct;
import org.itson.Alarma.Alarmas;
import org.itson.Anomalyzer.collections.Anomalia;
import org.itson.Anomalyzer.collections.Lectura;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.dtos.AnomaliaDTO;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.itson.Anomalyzer.persistence.IAnomaliasRepository;
import org.itson.Anomalyzer.proto.ClienteAlarmasGrpc;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Analizador {
    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    IAnomaliasRepository anomaliasRepository;

    // Mapa para llevar el conteo de anomalías seguidas por sensor
    private List<AlarmaDTO> alarmas = new ArrayList<>();
    private Map<String, Integer> contadorAnomalias = new HashMap<>();
    private Map<String, Integer> contadorNormales = new HashMap<>();
    private Map<String, List<Lectura>> lecturasAnomalasPorSensor = new HashMap<>();
    private static final String QUEUE_SEND = "anomalias";

    @PostConstruct
    public void inicializarAlarmas() {
        List<Alarmas.AlarmaDTO> alarmasObtenidas = clienteAlarmasGrpc.obtenerAlarmas();

        for (Alarmas.AlarmaDTO alarmasRecibida : alarmasObtenidas) {
            AlarmaDTO alarmaDTO = new AlarmaDTO();
            alarmaDTO.setIdAlarma(alarmasRecibida.getIdAlarma());
            alarmaDTO.setSensores(alarmasRecibida.getIdSensoresList());
            alarmaDTO.setInvernadero(alarmasRecibida.getInvernadero());
            alarmaDTO.setValorMinimo(alarmasRecibida.getValorMinimo());
            alarmaDTO.setValorMaximo(alarmasRecibida.getValorMaximo());
            alarmaDTO.setActivo(alarmasRecibida.getActivo());
            agregarAlarma(alarmaDTO);
        }
    }

    public void procesarLectura(LecturaDTO lecturaEnriquecida) {
        try {
            if (alarmas.isEmpty()) {
                System.out.println("Llegó una lectura, pero no hay alarmas registradas (no se dispararán anomalías).");
                return;
            }

            List<AlarmaDTO> alarmasActivas = obtenerAlarmasActivas(alarmas);
            String idSensor = lecturaEnriquecida.getIdSensor();
            String magnitud = lecturaEnriquecida.getMagnitud();
            String nombreInvernadero = lecturaEnriquecida.getNombreInvernadero();
            float valor = lecturaEnriquecida.getValor();

            int coincidenciasAnomalias = 0;

            for (AlarmaDTO alarma : alarmasActivas) {
                if (verificarSensor(alarma, idSensor)
                        && verificarInvernadero(alarma, nombreInvernadero)
                        && verificarValor(alarma, valor)) {
                    coincidenciasAnomalias++;
                }
            }

            if (coincidenciasAnomalias > 0) {
                int acumuladas = contadorAnomalias.getOrDefault(idSensor, 0) + coincidenciasAnomalias;
                contadorAnomalias.put(idSensor, acumuladas);
                contadorNormales.put(idSensor, 0); // reset normales

                // Acumular la lectura anómala
                Lectura lectura = new Lectura(lecturaEnriquecida);
                // Agregar la lectura anómala a la lista del sensor correspondiente
                if (!lecturasAnomalasPorSensor.containsKey(idSensor)) {
                    lecturasAnomalasPorSensor.put(idSensor, new ArrayList<>());
                }
                lecturasAnomalasPorSensor.get(idSensor).add(lectura);

                System.out.println("Anomalías detectadas (" + coincidenciasAnomalias + ") en sensor " + idSensor +
                        ". Contador acumulado: " + acumuladas);

                // Si se acumulan 5 o más lecturas anómalas, se envía la anomalía
                if (acumuladas >= 5) {
                    List<Lectura> lecturasAnomalias = lecturasAnomalasPorSensor.getOrDefault(idSensor, new ArrayList<>());

                    Anomalia anomalia = new Anomalia();
                    anomalia.setLecturas(new ArrayList<>(lecturasAnomalias));

                    // Obtener la alarma coincidente
                    AlarmaDTO alarmaCoincidente =
                            alarmasActivas.stream() // Filtrar las alarmas activas
                            .filter(a -> verificarSensor(a, idSensor) && verificarInvernadero(a, nombreInvernadero)) // Filtrar por sensor e invernadero
                            .findFirst() // Obtener la primera alarma que coincida
                            .orElse(null); // Si no hay coincidencias, se asigna null

                    String causa = "";
                    if (alarmaCoincidente != null) {
                        float valorUltimaLectura = lecturasAnomalias.get(lecturasAnomalias.size() - 1).getValor();
                        float minimo = alarmaCoincidente.getValorMinimo();
                        float maximo = alarmaCoincidente.getValorMaximo();

                        if (valorUltimaLectura < minimo) {
                            if (magnitud.equalsIgnoreCase("humedad")) {
                                causa = "La humedad ha bajado del " + minimo + "%";
                            } else if (magnitud.equalsIgnoreCase("temperatura")) {
                                causa = "La temperatura ha disminuido de los " + minimo + "°C";
                            }
                        } else if (valorUltimaLectura > maximo) {
                            if (magnitud.equalsIgnoreCase("humedad")) {
                                causa = "La humedad ha subido del " + maximo + "%";
                            } else if (magnitud.equalsIgnoreCase("temperatura")) {
                                causa = "La temperatura ha superado los " + maximo + "°C";
                            }
                        }
                    }

                    anomalia.setCausa(causa);
                    anomaliasRepository.save(anomalia);

                    AnomaliaDTO anomaliaDTO = convertirAnomaliaDTO(anomalia);
                    rabbitTemplate.convertAndSend(QUEUE_SEND, anomaliaDTO);
                    System.out.println("Anomalía enviada a la cola 'anomalias'.");

                    // Reset
                    contadorAnomalias.put(idSensor, 0);
                    lecturasAnomalasPorSensor.remove(idSensor);
                }

            } else {
                int normales = contadorNormales.getOrDefault(idSensor, 0) + 1;
                contadorNormales.put(idSensor, normales);
                contadorAnomalias.put(idSensor, 0); // reset anomalías
                lecturasAnomalasPorSensor.remove(idSensor); // también limpiar

                System.out.println("Lectura normal en sensor " + idSensor +
                        ". Contador normales: " + normales);

                if (normales >= 5) {
                    contadorNormales.put(idSensor, 0);
                    contadorAnomalias.put(idSensor, 0);
                }
            }

        } catch (Exception e) {
            System.err.println("Error al procesar lectura enriquecida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private AnomaliaDTO convertirAnomaliaDTO(Anomalia anomalia) {
        AnomaliaDTO anomaliaDTO = new AnomaliaDTO(
                anomalia.get_id().toHexString(),
                convertirAnomaliaDTO(anomalia.getLecturas()),
                anomalia.getCausa()
        );

        return anomaliaDTO;
    }

    private List<LecturaDTO> convertirAnomaliaDTO(List<Lectura> lecturas) {
        List<LecturaDTO> lecturasDTO = new ArrayList<>();
        for (Lectura lectura : lecturas) {
            LecturaDTO lecturaDTO = new LecturaDTO(lectura);
            lecturasDTO.add(lecturaDTO);
        }
        return lecturasDTO;
    }

    private List<AlarmaDTO> obtenerAlarmasActivas(List<AlarmaDTO> alarmas) {
        List<AlarmaDTO> alarmasActivas = new ArrayList<>();
        for (AlarmaDTO alarma : alarmas) {
            if (alarma.isActivo()) {
                alarmasActivas.add(alarma);
            }
        }
        return alarmasActivas;
    }

    public boolean verificarSensor(AlarmaDTO alarma, String idSensor) {
        return alarma.getSensores().contains(idSensor);
    }

    public boolean verificarInvernadero(AlarmaDTO alarma, String invernadero) {
        return alarma.getInvernadero().equals(invernadero);
    }

    public boolean verificarValor(AlarmaDTO alarma, float valor) {
        return valor < alarma.getValorMinimo() || valor > alarma.getValorMaximo();
    }

    public void agregarAlarma(AlarmaDTO alarma) {
        alarmas.add(alarma);
    }

    public void actualizarAlarma(AlarmaDTO alarmaActualizada) {
        for (AlarmaDTO alarma : alarmas) {
            if (alarma.getIdAlarma().equals(alarmaActualizada.getIdAlarma())) {
                alarma.setSensores(alarmaActualizada.getSensores());
                alarma.setInvernadero(alarmaActualizada.getInvernadero());
                alarma.setValorMinimo(alarmaActualizada.getValorMinimo());
                alarma.setValorMaximo(alarmaActualizada.getValorMaximo());
                alarma.setActivo(alarmaActualizada.isActivo());
                break;
            }
        }
    }

    public void eliminarAlarma(AlarmaDTO alarmaEliminar) {
        for (AlarmaDTO alarma : alarmas) {
            if (alarma.getIdAlarma().equals(alarmaEliminar.getIdAlarma())) {
                alarmas.remove(alarma);
                break;
            }
        }
    }

    public void actualizarAlarmas(List<AlarmaDTO> alarmasDTO) {
        for (AlarmaDTO nuevaAlarma : alarmasDTO) {
            boolean encontrada = false;

            for (AlarmaDTO alarmaExistente : alarmas) {
                if (alarmaExistente.getIdAlarma().equals(nuevaAlarma.getIdAlarma())) {
                    // Actualizar los campos de la alarma existente
                    actualizarAlarma(nuevaAlarma);
                    encontrada = true;
                    break;
                }
            }

            if (!encontrada) {
                // Si no existe, se agrega la nueva alarma
                agregarAlarma(nuevaAlarma);
            }
        }
    }
}
