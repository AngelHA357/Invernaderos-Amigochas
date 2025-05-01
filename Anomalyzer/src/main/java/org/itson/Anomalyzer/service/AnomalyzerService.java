package org.itson.Anomalyzer.service;

import org.bson.types.ObjectId;
import org.itson.Alarma.Alarmas;
import org.itson.Anomalyzer.collections.Anomalia;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.dtos.AnomaliaDTO;
import org.itson.Anomalyzer.persistence.IAnomaliasRepository;
import org.itson.Anomalyzer.proto.ClienteAlarmasGrpc;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyzerService {
    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;
    @Autowired
    IAnomaliasRepository anomaliasRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String QUEUE_SEND = "anomalias";

    public List<AlarmaDTO> obtenerAlarmas() {
        List<Alarmas.AlarmaDTO> alarmasObtenidasGrpc = clienteAlarmasGrpc.obtenerAlarmas();
        List<AlarmaDTO> alarmasDTO = new ArrayList<>();
        for (Alarmas.AlarmaDTO alarmaGrpc : alarmasObtenidasGrpc) {
            AlarmaDTO alarmaDTO = new AlarmaDTO();
            alarmaDTO.setIdAlarma(alarmaGrpc.getIdAlarma());
            alarmaDTO.setSensores(alarmaGrpc.getIdSensoresList());
            alarmaDTO.setInvernadero(alarmaGrpc.getInvernadero());
            alarmaDTO.setValorMinimo(alarmaGrpc.getValorMinimo());
            alarmaDTO.setValorMaximo(alarmaGrpc.getValorMaximo());
            alarmaDTO.setActivo(alarmaGrpc.getActivo());

            alarmasDTO.add(alarmaDTO);
        }

        return alarmasDTO;
    }

    public void guardarAnomalia(AnomaliaDTO anomaliaDTO) {
        Anomalia anomalia = convertirAnomalia(anomaliaDTO);
        anomaliasRepository.save(anomalia);
        System.out.println("Anomalia guardada con éxito en la base de datos.");
    }

    public void emitirAnomalia(AnomaliaDTO anomalia) {
        rabbitTemplate.convertAndSend(QUEUE_SEND, anomalia);
    }

    private Anomalia convertirAnomalia(AnomaliaDTO anomaliaDTO) {
        Anomalia anomalia = new Anomalia(
                anomaliaDTO.getIdSensor(),
                anomaliaDTO.getMacAddress(),
                anomaliaDTO.getMarca(),
                anomaliaDTO.getModelo(),
                anomaliaDTO.getMagnitud(),
                anomaliaDTO.getUnidad(),
                anomaliaDTO.getValor(),
                anomaliaDTO.getFechaHora(),
                new ObjectId(anomaliaDTO.getIdInvernadero()),
                anomaliaDTO.getNombreInvernadero(),
                anomaliaDTO.getSector(),
                anomaliaDTO.getFila(),
                anomaliaDTO.getCausa()
        );
        return anomalia;
    }
}
