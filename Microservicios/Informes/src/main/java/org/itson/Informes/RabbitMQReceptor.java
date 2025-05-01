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
import org.springframework.dao.DataAccessException;

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
            factory.setHost("localhost");
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

                        System.out.println(" [RECEPTOR INFORMES] Entidad ANTES de guardar: " + entidadParaGuardar.toString());

                        InformeLectura guardada = informeLecturaRepository.save(entidadParaGuardar);

                        if (guardada != null) {
                            System.out.println(" [RECEPTOR INFORMES] Entidad DESPUÉS de guardar (devuelta por save): " + guardada.toString());
                            System.out.println(" [RECEPTOR INFORMES] Lectura guardada localmente en 'lecturas_informes_db' con ID: " + guardada.get_id());
                        } else {
                            System.err.println(" [!] ERROR RARO: informeLecturaRepository.save() devolvió NULL.");
                        }

                    } catch (com.google.gson.JsonSyntaxException jsonEx) {
                        System.err.println(" [!] Error al deserializar JSON de RabbitMQ: " + jsonEx.getMessage());
                    } catch (org.springframework.dao.DataAccessException dataEx) {
                        System.err.println(" [!] ERROR DE ACCESO A DATOS (MongoDB?) al guardar: " + dataEx.getMessage());
                        if (dataEx.getRootCause() != null) {
                            System.err.println(" [!] Causa Raíz del Error de Datos: " + dataEx.getRootCause().getMessage());
                        }
                        dataEx.printStackTrace();
                    } catch (Exception e) {
                        System.err.println(" [!] Error General procesando mensaje de RabbitMQ: " + e.getMessage());
                        e.printStackTrace();
                    }
                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {});

                System.out.println(" [RECEPTOR INFORMES] Consumidor iniciado. Esperando... (Este hilo se bloqueará aquí)");

                Thread.currentThread().join();

            } catch (Exception e) {
                System.err.println(" [RECEPTOR INFORMES] Error CRÍTICO en la conexión/consumo de RabbitMQ: " + e.getMessage());
                e.printStackTrace();
            }

        }).start();

    }
}