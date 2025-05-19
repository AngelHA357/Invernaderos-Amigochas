package org.itson.Anomalyzer.service;

import jakarta.annotation.PostConstruct;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.dtos.AnomaliaDTO;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Analizador {
    @Autowired
    AnomalyzerService anomalyzerService;

    // Lista de alarmas.
    private List<AlarmaDTO> alarmas = new ArrayList<>();

    // Mapa para contar lecturas anómalas por sensor.
    private Map<String, Integer> lecturasAnomalasPorSensor = new HashMap<>();

    // Límite de lecturas anómalas para disparar una alarma.
    private static final int LIMITE_LECTURAS_ANOMALAS = 5;

    @PostConstruct
    public void inicializarAlarmas() {
        List<AlarmaDTO> alarmasObtenidas = anomalyzerService.obtenerAlarmas();
        agregarAlarmas(alarmasObtenidas);
    }

    public void procesarLectura(LecturaDTO lectura) {
        try {
            if (alarmas.isEmpty()) {
                System.out.println("Llegó una lectura, pero no hay alarmas registradas (no se dispararán anomalías).");
                return;
            }

            List<AlarmaDTO> alarmasActivas = obtenerAlarmasActivas();
            String idSensor = lectura.getIdSensor();
            String nombreInvernadero = lectura.getNombreInvernadero();
            AlarmaDTO alarmaDetonadora = null;
            float valor = lectura.getValor();

            boolean coincidenciasAnomalias = false;

            for (AlarmaDTO alarma : alarmasActivas) {
                if (verificarSensor(alarma, idSensor) &&
                        verificarInvernadero(alarma, nombreInvernadero) &&
                        verificarValor(alarma, valor)) {
                    System.out.println("Lectura anómala detectada en el sensor " + idSensor + " en el " + nombreInvernadero);
                    if (lecturasAnomalasPorSensor.containsKey(idSensor)) {
                        lecturasAnomalasPorSensor.put(idSensor, lecturasAnomalasPorSensor.get(idSensor) + 1);
                    } else {
                        lecturasAnomalasPorSensor.put(idSensor, 1);
                    }
                    coincidenciasAnomalias = true;
                    alarmaDetonadora = alarma;
                }
            }

            if (!coincidenciasAnomalias) {
                return;
            }
            if (lecturasAnomalasPorSensor.get(idSensor) == LIMITE_LECTURAS_ANOMALAS) {
                String magnitud = lectura.getMagnitud();
                String unidad = lectura.getUnidad();
                float maximo = alarmaDetonadora.getValorMaximo();
                float minimo = alarmaDetonadora.getValorMinimo();
                AnomaliaDTO anomalia = new AnomaliaDTO(lectura);
                if (lectura.getValor() > alarmaDetonadora.getValorMaximo()) {
                    anomalia.setCausa("Valores de " + magnitud.toLowerCase() + " por encima del máximo (" + maximo + unidad + ") permitido.");
                } else if (lectura.getValor() < alarmaDetonadora.getValorMinimo()) {
                    anomalia.setCausa("Valores de " + magnitud.toLowerCase() + " por debajo del mínimo (" + minimo + unidad + ") permitido.");
                }

                imprimirAnomalia(anomalia);

                anomalyzerService.guardarAnomalia(anomalia);
                anomalyzerService.enviarNotificacion(alarmaDetonadora, anomalia);
                anomalyzerService.desactivarAlarma(alarmaDetonadora);
            }

        } catch (Exception e) {
            System.err.println("Error al procesar lectura enriquecida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<AlarmaDTO> obtenerAlarmasActivas() {
        List<AlarmaDTO> alarmasActivas = new ArrayList<>();
        for (AlarmaDTO alarma : this.alarmas) {
            if (alarma.isActivo()) {
                alarmasActivas.add(alarma);
            }
        }
        return alarmasActivas;
    }

    public boolean verificarSensor(AlarmaDTO alarma, String idSensor) {
        return alarma.getSensores().contains(idSensor);
    }

    public boolean verificarInvernadero(AlarmaDTO alarma, String nombreInvernadero) {
        return alarma.getInvernadero().equals(nombreInvernadero);
    }

    public boolean verificarValor(AlarmaDTO alarma, float valor) {
        return valor < alarma.getValorMinimo() || valor > alarma.getValorMaximo();
    }

    private void agregarAlarmas(List<AlarmaDTO> alarmas) {
        for (AlarmaDTO alarma : alarmas) {
            agregarAlarma(alarma);
        }
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

    // Método para imprimir la información de la anomalía
    private void imprimirAnomalia(AnomaliaDTO anomalia) {
        String idSensor = anomalia.getIdSensor();
        String macAddress = anomalia.getMacAddress();
        String marca = anomalia.getMarca();
        String modelo = anomalia.getModelo();
        String magnitud = anomalia.getMagnitud();
        String unidad = anomalia.getUnidad();
        String nombreInvernadero = anomalia.getNombreInvernadero();
        String sector = anomalia.getSector();
        String fila = anomalia.getFila();
        float valor = anomalia.getValor();
        Date fechaHora = anomalia.getFechaHora();
        String causa = anomalia.getCausa();
        String fechaHoraFormateada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaHora);

        System.out.printf("""
                ┌-------------------------------------------------------------------------------------------------┐
                |                                        ANOMALíA GUARDADA                                        |
                ├------------------┬------------------------------------------------------------------------------┤
                | ID sensor        | %-76s |
                | MAC address      | %-76s |
                | Marca            | %-76s |
                | Modelo           | %-76s |
                | Magnitud         | %-76s |
                | Valor            | %-76.2f |
                | Unidad           | %-76s |
                | Invernadero      | %-76s |
                | Sector           | %-76s |
                | Fila             | %-76s |
                | Causa            | %-76s |
                | Hora             | %-76s |
                └------------------┴------------------------------------------------------------------------------┘
                """, idSensor, macAddress, marca, modelo, magnitud, valor, unidad, nombreInvernadero, sector, fila, causa, fechaHoraFormateada);
    }
}
