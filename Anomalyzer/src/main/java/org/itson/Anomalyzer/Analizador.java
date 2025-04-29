package org.itson.Anomalyzer;

import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.itson.Alarma.Alarmas;
import org.itson.Anomalyzer.collections.Anomalia;
import org.itson.Anomalyzer.collections.Lectura;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.itson.Anomalyzer.persistence.IAnomaliasRepository;
import org.itson.Anomalyzer.proto.ClienteAlarmasGrpc;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Analizador {
    List<AlarmaDTO> alarmas = new ArrayList<>();

    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    // Mapa para llevar el conteo de anomalías seguidas por sensor
    private Map<String, Integer> contadorAnomalias = new HashMap<>();
    private Map<String, Integer> contadorNormales = new HashMap<>();
    private Map<String, List<Lectura>> lecturasAnomalasPorSensor = new HashMap<>();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    IAnomaliasRepository anomaliasRepository;

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
            imprimirLectura(lecturaEnriquecida);

            if (alarmas.isEmpty()) {
                System.out.println("No hay alarmas registradas.");
                return;
            }

            List<AlarmaDTO> alarmasActivas = filtrarAlarmasActivas(alarmas);
            String idSensor = lecturaEnriquecida.getIdSensor();
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

                if (acumuladas >= 5) {
                    List<Lectura> lecturasAnomalias = lecturasAnomalasPorSensor.getOrDefault(idSensor, new ArrayList<>());

                    Anomalia anomalia = new Anomalia();
                    anomalia.setLecturas(new ArrayList<>(lecturasAnomalias));
                    anomalia.setDescripcion("Se detectaron 5 o más lecturas anómalas para el sensor " + idSensor);

                    rabbitTemplate.convertAndSend("anomalias", anomalia);
                    System.out.println("Anomalía enviada a la cola 'anomalias'.");

                    anomaliasRepository.save(anomalia);

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

    private List<AlarmaDTO> filtrarAlarmasActivas(List<AlarmaDTO> alarmas) {
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

    // Método para imprimir la información de la lectura
    private void imprimirLectura(LecturaDTO lectura) {
        String idSensor = lectura.getIdSensor();
        String macAddress = lectura.getMacAddress();
        String marca = lectura.getMarca();
        String modelo = lectura.getModelo();
        String magnitud = lectura.getMagnitud();
        String unidad = lectura.getUnidad();
        String nombreInvernadero = lectura.getNombreInvernadero();
        String sector = lectura.getSector();
        String fila = lectura.getFila();
        float valor = lectura.getValor();
        Date fechaHora = lectura.getFechaHora();
        String fechaHoraFormateada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaHora);

        System.out.printf("""
                ┌-----------------------------------------------------------┐
                |                     LECTURA GUARDADA                      |
                ├------------------┬----------------------------------------┤
                | ID sensor        | %-38s |
                | MAC address      | %-38s |
                | Marca            | %-38s |
                | Modelo           | %-38s |
                | Magnitud         | %-38s |
                | Valor            | %-38.2f |
                | Unidad           | %-38s |
                | Invernadero      | %-38s |
                | Sector           | %-38s |
                | Fila             | %-38s |
                | Hora             | %-38s |
                └------------------┴----------------------------------------┘
                """, idSensor, macAddress, marca, modelo, magnitud, valor, unidad, nombreInvernadero, sector, fila, fechaHoraFormateada);
    }
}
