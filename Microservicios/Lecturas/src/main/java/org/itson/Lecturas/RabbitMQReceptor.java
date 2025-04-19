package org.itson.Lecturas;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RabbitMQReceptor {

    @Autowired
    private ProcesadorLecturas procesadorLecturas;
    private static final String QUEUE_NAME = "lecturas_crudas";
    private final ConcurrentHashMap<String, LecturaDTO> lecturasPorSensor = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> estadoSensores = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Iniciar el guardador de lecturas con nuestra referencia atómica
        procesadorLecturas.iniciar(lecturasPorSensor);

        // Thread para recibir mensajes de RabbitMQ
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println("Esperando lecturas...");

                // Consumidor de RabbitMQ - recibe lecturas continuamente
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    Gson gson = new Gson();
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);
                    // Si el valor asociado al ID sensor es positivo, está activo, si no, está desactivado.
                    //if (estadoSensores.get(lectura.getIdSensor())) {
                        lecturasPorSensor.put(lectura.getIdSensor(), lectura);
                        System.out.println("Lectura recibida");
                    //}
                };

                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                });

                // Esperar indefinidamente para mantener el hilo principal vivo
                Thread.currentThread().join();
            } catch (Exception e) {
                System.err.println("Error en el receptor: " + e.getMessage());
            }
        }).start();
    }
}