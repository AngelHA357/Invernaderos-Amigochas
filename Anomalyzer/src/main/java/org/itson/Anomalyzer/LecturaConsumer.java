package org.itson.Anomalyzer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class LecturaConsumer {
    @Autowired
    Analizador analizador;

    private static final String QUEUE_RECEIVE = "lecturas_enriquecidas";
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando lecturas enriquecidas...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    LecturaDTO lecturaEnriquecida = gson.fromJson(mensaje, LecturaDTO.class);

                    analizador.procesarLectura(lecturaEnriquecida);
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
}