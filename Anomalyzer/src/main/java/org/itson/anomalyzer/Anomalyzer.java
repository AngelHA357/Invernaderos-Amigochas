/*
 * Anomalyzer.jar
 */
package org.itson.anomalyzer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.itson.dtos.LecturaDTO;

/**
 * Clase principal de Anomalyzer. Se encarga de recoger todos los datos tirados
 * por el Gateway físico.
 *
 * @author Equipazo 1
 */
public class Anomalyzer {
    private static final String QUEUE_NAME = "mediciones";
    private static volatile boolean running = true;

    public static void main(String[] args) {
        // Crear conexión y canal
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            // Asegurar que la cola existe
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Esperando mensajes...");
            
            // Consumidor
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensaje = new String(delivery.getBody(), "UTF-8");
                System.out.println("Medición recibida");
                // Deserializar a objeto LecturaDTO
                Gson gson = new Gson();
                LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);
                // Mostrar los datos recibidos
                System.out.println("ID Sensor: " + lectura.getIdSensor());
                System.out.println("Tipo: " + lectura.getTipoSensor());
                System.out.println("Valor: " + lectura.getValor());
                System.out.println("Timestamp: " + lectura.getTimestamp());
                System.out.println("---------------\n");
            };
            
            while (true) {
            // Iniciar consumo
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            }
        } catch (IOException | TimeoutException e) {
            System.err.println("Error en el receptor: " + e.getMessage());
        }
    }
}