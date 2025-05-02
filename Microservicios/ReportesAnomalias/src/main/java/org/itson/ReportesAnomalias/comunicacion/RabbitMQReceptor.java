package org.itson.ReportesAnomalias.comunicacion;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.ReportesAnomalias.dtos.AnomaliaDTO;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasServiceException;
import org.itson.ReportesAnomalias.service.ReportesAnomaliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQReceptor {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ReportesAnomaliasService service;

    private final Gson gson = new Gson();

    private static final String QUEUE_RECEIVE = "anomalias";

    @PostConstruct
    public void init() {
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando anomalias...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    AnomaliaDTO anomalia = gson.fromJson(mensaje, AnomaliaDTO.class);

                    System.out.println("Anomalía recibida");

                    try {
                        service.registrarAnomalia(anomalia);
                    } catch (ReportesAnomaliasServiceException e) {
                        System.err.println("Error en el receptor: " + e.getMessage());
                    }
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
