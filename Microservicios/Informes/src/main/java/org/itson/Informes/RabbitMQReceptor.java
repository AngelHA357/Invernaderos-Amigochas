package org.itson.Informes;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Informes.collections.InformeLectura;
import org.itson.Informes.dtos.LecturaDTO;
import org.itson.Informes.persistence.IInformeLecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQReceptor {

    @Autowired
    private IInformeLecturasRepository informeLecturaRepository;

    private static final String QUEUE_RECEIVE = "lecturas_enriquecidas";

    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {

        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            System.out.println(" [RECEPTOR INFORMES] Intentando conectar a RabbitMQ en " + factory.getHost() + "...");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println(" [RECEPTOR INFORMES] Conectado. Esperando mensajes en cola '" + QUEUE_RECEIVE + "'...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println(" [RECEPTOR INFORMES] Mensaje recibido.");

                    try {
                        LecturaDTO lecturaRecibida = gson.fromJson(mensaje, LecturaDTO.class);

                        InformeLectura entidadParaGuardar = new InformeLectura(lecturaRecibida);

                        InformeLectura guardada = informeLecturaRepository.save(entidadParaGuardar);

                        System.out.println(" [RECEPTOR INFORMES] Lectura guardada localmente en 'lecturas_informes_db' con ID: " + guardada.get_id());

                    } catch (com.google.gson.JsonSyntaxException jsonEx) {
                        System.err.println(" [RECEPTOR INFORMES] Error al deserializar JSON de RabbitMQ: " + jsonEx.getMessage());
                    } catch (Exception e) {
                        System.err.println(" [RECEPTOR INFORMES] Error procesando mensaje de RabbitMQ: " + e.getMessage());
                        e.printStackTrace();
                    }
                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {});

                Thread.currentThread().join();

            } catch (Exception e) {
                System.err.println(" [RECEPTOR INFORMES] Error en la conexión/consumo de RabbitMQ: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
