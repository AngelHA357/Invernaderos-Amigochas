package org.itson.Alarmator.comunication;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Alarmator.dtos.AlarmaAnomaliaDTO;
import org.itson.Alarmator.dtos.AnomaliaDTO;
import org.itson.Alarmator.encriptadores.EncriptadorRSA;
import org.itson.Alarmator.service.AlarmatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AnomaliaConsumer {
    @Autowired
    AlarmatorService alarmatorService;

    private static final String QUEUE_RECEIVE = "alarmas";
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando anomalías...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    try {
                        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/clave_privada_alarmator.pem");

                        PrivateKey llavePrivada = EncriptadorRSA.loadPrivateKey(inputStream);

                        String mensaje = EncriptadorRSA.decrypt(delivery.getBody(), llavePrivada);

                        AlarmaAnomaliaDTO alarmaAnomalia = gson.fromJson(mensaje, AlarmaAnomaliaDTO.class);
                        imprimirAnomalia(alarmaAnomalia.getAnomalia());

                        alarmatorService.dispararAlarma(alarmaAnomalia);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {
                });
                Thread.currentThread().join();
            } catch (Exception e) {
                System.err.println("Error en el receptor de lecturas enriquecidas: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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