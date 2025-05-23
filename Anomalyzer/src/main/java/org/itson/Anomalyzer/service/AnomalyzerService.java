package org.itson.Anomalyzer.service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.itson.Anomalyzer.collections.Alarma;
import org.itson.Anomalyzer.collections.Anomalia;
import org.itson.Anomalyzer.dtos.AlarmaAnomaliaDTO;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.dtos.AnomaliaDTO;
import org.itson.Anomalyzer.encriptadores.EncriptadorRSA;
import org.itson.Anomalyzer.persistence.anomalias.IAnomaliasRepository;
import org.itson.Anomalyzer.persistence.alarmas.IAlarmasRepository;
import org.itson.Anomalyzer.proto.ClienteAlarmasGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyzerService {
    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    @Autowired
    IAnomaliasRepository anomaliasRepository;

    private static final String QUEUE_ALARMAS = "alarmas";

    private final Gson gson = new Gson();

    private Channel channel;

    @PostConstruct
    private void init() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("password");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            System.err.println("Error al crear la cola: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarAnomalia(AnomaliaDTO anomaliaDTO) {
        Anomalia anomalia = convertirAnomalia(anomaliaDTO);
        anomaliasRepository.save(anomalia);
        System.out.println("Anomalia guardada con éxito en la base de datos.");
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
                anomaliaDTO.getIdInvernadero(),
                anomaliaDTO.getNombreInvernadero(),
                anomaliaDTO.getSector(),
                anomaliaDTO.getFila(),
                anomaliaDTO.getCausa()
        );
        return anomalia;
    }

    public void enviarNotificacion(AlarmaDTO alarmaDetonadora, AnomaliaDTO anomalia) {
        try {
            AlarmaAnomaliaDTO alarmaAnomalia = new AlarmaAnomaliaDTO(alarmaDetonadora, anomalia);
            String json = gson.toJson(alarmaAnomalia);

            // Abrimos el InputStream desde recursos en el classpath
            try (InputStream isPublicKey = getClass().getClassLoader().getResourceAsStream("keys/clave_publica_alarmator.pem")) {
                if (isPublicKey == null) {
                    throw new FileNotFoundException("No se encontró la llave pública en recursos");
                }
                PublicKey llavePublica = EncriptadorRSA.loadPublicKey(isPublicKey);

            String jsonEncriptado = EncriptadorRSA.encryptHybrid(json, llavePublica);

            channel.queueDeclare(QUEUE_ALARMAS, false, false, false, null);
            channel.basicPublish("", QUEUE_ALARMAS, null, jsonEncriptado.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Error al enviar la anomalía: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void desactivarAlarma(AlarmaDTO alarmaDetonadora) {
        clienteAlarmasGrpc.desactivarAlarma(alarmaDetonadora);
    }
}
