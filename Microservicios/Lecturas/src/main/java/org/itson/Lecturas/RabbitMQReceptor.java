package org.itson.Lecturas;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitMQReceptor {

    @Autowired
    private ProcesadorLecturas procesadorLecturas;

    private static final String QUEUE_RECEIVE = "lecturas";
    private final ConcurrentHashMap<String, LecturaDTO> lecturasPorSensor = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        procesadorLecturas.iniciar(lecturasPorSensor);

        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando lecturas...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);
                    System.out.println("Lectura recibida de la cola 'lecturas' para sensor: " + lectura.getIdSensor());
                    lecturasPorSensor.put(lectura.getIdSensor(), lectura);
                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {
                });
                Thread.currentThread().join();

            } catch (Exception e) {
                System.err.println("Error en el receptor: " + e.getMessage());
            }
        }).start();
    }
}