package org.itson.Alarmator.service;

import jakarta.annotation.PostConstruct;
import org.itson.Alarma.Alarmas;
import org.itson.Alarmator.dtos.AlarmaAnomaliaDTO;
import org.itson.Alarmator.dtos.AlarmaDTO;
import org.itson.Alarmator.dtos.AnomaliaDTO;
import org.itson.Alarmator.proto.ClienteAlarmasGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmatorService {
    @Autowired
    private ClienteAlarmasGrpc clienteAlarmasGrpc;
    private List<AlarmaDTO> alarmas = new ArrayList<>();

    @PostConstruct
    public void obtenerAlarmas() {
        List<Alarmas.AlarmaDTO> alarmasObtenidasGrpc = clienteAlarmasGrpc.obtenerAlarmas();
        for (Alarmas.AlarmaDTO alarmaGrpc : alarmasObtenidasGrpc) {
            AlarmaDTO alarmaDTO = new AlarmaDTO();
            alarmaDTO.setIdAlarma(alarmaGrpc.getIdAlarma());
            alarmaDTO.setSensores(alarmaGrpc.getIdSensoresList());
            alarmaDTO.setInvernadero(alarmaGrpc.getInvernadero());
            alarmaDTO.setValorMinimo(alarmaGrpc.getValorMinimo());
            alarmaDTO.setValorMaximo(alarmaGrpc.getValorMaximo());
            alarmaDTO.setActivo(alarmaGrpc.getActivo());

            alarmas.add(alarmaDTO);
        }
    }

    public void dispararAlarma(AlarmaAnomaliaDTO alarmaAnomalia) {
        AlarmaDTO alarma = alarmaAnomalia.getAlarma();
        AnomaliaDTO anomalia = alarmaAnomalia.getAnomalia();

        if (alarma.getMedioNotificacion().equalsIgnoreCase("Email")) {
            // Enviar correo electrónico
        } else if (alarma.getMedioNotificacion().equalsIgnoreCase("SMS")) {
            // Enviar SMS
        }
        System.out.println("Alarma disparada: " + alarma.getIdAlarma());
        System.out.println("Anomalia: " + anomalia.getIdSensor() + " - " + anomalia.getValor() + " " + anomalia.getUnidad());
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
