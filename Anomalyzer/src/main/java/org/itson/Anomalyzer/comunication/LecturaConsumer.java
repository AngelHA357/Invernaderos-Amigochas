package org.itson.Anomalyzer.comunication;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.itson.Anomalyzer.encriptadores.EncriptadorRSA;
import org.itson.Anomalyzer.service.Analizador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.PrivateKey;
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
            factory.setHost("rabbitmq");
            factory.setPort(5672);
            factory.setUsername("user");
            factory.setPassword("password");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel();) {
                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando lecturas enriquecidas...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    try {
                        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/clave_privada_anomalyzer.pem");

                        PrivateKey llavePrivada = EncriptadorRSA.loadPrivateKey(inputStream);

                        String mensaje = EncriptadorRSA.decryptHybrid(new String(delivery.getBody()), llavePrivada);

                        LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);
                        analizador.procesarLectura(lectura);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {
                });
                latch.await(); // mantiene el hilo vivo
            } catch (Exception e) {
                System.err.println("Error en el receptor de lecturas enriquecidas: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}