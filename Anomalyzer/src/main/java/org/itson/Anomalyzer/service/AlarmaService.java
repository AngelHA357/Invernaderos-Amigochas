package org.itson.Anomalyzer.service;

import org.itson.Anomalyzer.collections.Alarma;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.persistence.alarmas.IAlarmasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmaService {
    @Autowired
    private IAlarmasRepository alarmasRepository;

    public List<AlarmaDTO> obtenerAlarmas() {
        List<Alarma> alarmasObtenidas = alarmasRepository.findAll();
        List<AlarmaDTO> alarmasDTO = new ArrayList<>();
        for (Alarma alarma : alarmasObtenidas) {
            AlarmaDTO alarmaDTO = new AlarmaDTO();
            alarmaDTO.setIdAlarma(alarma.getIdAlarma());
            alarmaDTO.setSensores(alarma.getSensores());
            alarmaDTO.setInvernadero(alarma.getInvernadero());
            alarmaDTO.setValorMinimo(alarma.getValorMinimo());
            alarmaDTO.setValorMaximo(alarma.getValorMaximo());
            alarmaDTO.setMagnitud(alarma.getMagnitud());
            alarmaDTO.setUnidad(alarma.getUnidad());
            alarmaDTO.setMedioNotificacion(alarma.getMedioNotificacion());
            alarmaDTO.setActivo(alarma.isActivo());

            alarmasDTO.add(alarmaDTO);
        }

        return alarmasDTO;
    }
}
