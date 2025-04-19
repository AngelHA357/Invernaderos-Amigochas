package org.itson.Broker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.itson.Broker.dtos.LecturaDTO;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQEmisor {
    private static final String OUTGOING_QUEUE = "lecturas_procesadas";

    public void enviarLectura(LecturaDTO lectura) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(OUTGOING_QUEUE, false, false, false, null);
            String mensajeJson = new Gson().toJson(lectura);
            channel.basicPublish("", OUTGOING_QUEUE, null, mensajeJson.getBytes(StandardCharsets.UTF_8));
            System.out.println("Lectura reenviada a la cola '" + OUTGOING_QUEUE + "'");

        } catch (Exception e) {
            System.err.println("Error al reenviar lectura: " + e.getMessage());
        }
    }
}
