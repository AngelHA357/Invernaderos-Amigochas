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
import java.text.SimpleDateFormat;
import java.util.Date;

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

                    imprimirLectura(lecturaEnriquecida);

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

    // Método para imprimir la información de la lectura
    private void imprimirLectura(LecturaDTO lectura) {
        String idSensor = lectura.getIdSensor();
        String macAddress = lectura.getMacAddress();
        String marca = lectura.getMarca();
        String modelo = lectura.getModelo();
        String magnitud = lectura.getMagnitud();
        String unidad = lectura.getUnidad();
        String nombreInvernadero = lectura.getNombreInvernadero();
        String sector = lectura.getSector();
        String fila = lectura.getFila();
        float valor = lectura.getValor();
        Date fechaHora = lectura.getFechaHora();
        String fechaHoraFormateada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaHora);

        System.out.printf("""
                ┌-----------------------------------------------------------┐
                |                     LECTURA GUARDADA                      |
                ├------------------┬----------------------------------------┤
                | ID sensor        | %-38s |
                | MAC address      | %-38s |
                | Marca            | %-38s |
                | Modelo           | %-38s |
                | Magnitud         | %-38s |
                | Valor            | %-38.2f |
                | Unidad           | %-38s |
                | Invernadero      | %-38s |
                | Sector           | %-38s |
                | Fila             | %-38s |
                | Hora             | %-38s |
                └------------------┴----------------------------------------┘
                """, idSensor, macAddress, marca, modelo, magnitud, valor, unidad, nombreInvernadero, sector, fila, fechaHoraFormateada);
    }
}