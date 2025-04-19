package org.itson.Broker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Broker.dtos.LecturaDTO;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitMQReceptor {
    private static final String QUEUE_NAME = "lecturas_crudas";
    private static final ConcurrentHashMap<String, Boolean> estadoSensores = new ConcurrentHashMap<>();

    private final RabbitMQEmisor emisor;

    public RabbitMQReceptor(RabbitMQEmisor emisor) {
        this.emisor = emisor;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println("Esperando lecturas...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    Gson gson = new Gson();
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);

                    if (estadoSensores.getOrDefault(lectura.getIdSensor(), true)) {
                        System.out.println("Lectura recibida: " + mensaje);
                        emisor.enviarLectura(lectura);
                    }
                };

                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
                Thread.currentThread().join();

            } catch (Exception e) {
                System.err.println("Error en el receptor: " + e.getMessage());
            }
        }).start();
    }
}