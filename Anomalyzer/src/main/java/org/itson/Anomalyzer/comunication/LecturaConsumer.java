package org.itson.Anomalyzer.comunication;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Anomalyzer.service.Analizador;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@Component
public class LecturaConsumer {
    @Autowired
    Analizador analizador;

    private static final String QUEUE_RECEIVE = "lecturas";
    private final Gson gson = new Gson();


    @PostConstruct
    public void init() {
        new Thread(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel();) {
                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando lecturas enriquecidas...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);

                    analizador.procesarLectura(lectura);
                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {});
                latch.await(); // mantiene el hilo vivo
            } catch (Exception e) {
                System.err.println("Error en el receptor de lecturas enriquecidas: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}