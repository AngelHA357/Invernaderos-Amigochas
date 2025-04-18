package org.itson.Lecturas;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Lecturas.controller.LecturasController;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RabbitMQReceptor {

    private static final String QUEUE_NAME = "mediciones";

    @Autowired
    private LecturasController controller;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel();) {

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println("Esperando mensajes...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Lectura recibida");
                    Gson gson = new Gson();
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);
                    controller.registrarLectura(lectura);

                    System.out.println("ID Lectura: " + lectura.getIdSensor());
                    System.out.println("Tipo: " + lectura.getTipoSensor());
                    System.out.println("Valor: " + lectura.getValor());
                    System.out.println("Timestamp: " + lectura.getTimestamp());
                    System.out.println("---------------\n");
                };

                while (true) {
                    Thread.sleep(Duration.ofSeconds(5L));
                    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
                }
            } catch (Exception e) {
                System.err.println("Error en el receptor: " + e.getMessage());
            }
        }).start();
    }
}
