/*
 * Gateway.java
 */
package org.itson.gateway;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.itson.dtos.MedicionDTO;

/**
 *
 * @author ricar
 */
public class Gateway {

    private static final String QUEUE_NAME = "mediciones";

    public static void main(String[] args) {
        try {
            // Mosquitto/MQTT
            String brokerIP = "localhost"; // IP de Mosquitto (localhost si es en local)
            String broker = "tcp://" + brokerIP + ":1883"; // URI del servidor Mosquitto
            String clientId = MqttClient.generateClientId(); // ID del cliente
            MqttClient client = new MqttClient(broker, clientId); // Creamos el cliente

            // RabbitMQ
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost"); // Indicamos la IP del host
            Connection rabbitConnection = factory.newConnection(); // Creamos la conexión
            Channel channel = rabbitConnection.createChannel(); // Creamos el canal
            channel.queueDeclare(QUEUE_NAME, false, false, false, null); // Declaramos la cola

            // Para serializar
            Gson gson = new Gson();

            // Para usar varios hilos
            ExecutorService executor = Executors.newFixedThreadPool(4);

            // Definir el comportamiento cuando llegan mensajes o se pierde conexión
            client.setCallback(new MqttCallback() {
                /**
                 * Método llamado cuando se pierde la conexión con el broker.
                 */
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Conexión perdida: " + cause.getMessage());
                }

                /**
                 * Método llamado cuando llega un mensaje al topic suscrito.
                 *
                 * @param topic El topic del mensaje.
                 * @param message El mensaje recibido.
                 */
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    /**
                     * Usamos ExecutorService aquí por si se tarda en publicar
                     * el mensaje en Rabbit y que no se bloquee el hilo.
                     */
                    executor.submit(() -> {
                        try {
                            // Mensaje recibido
                            String payload = message.toString();
                            System.out.println("Mensaje recibido en " + topic + ": " + payload);

                            // Separar el sensor y el dato
                            String[] partes = payload.split("/");

                            if (partes.length != 2) {
                                System.err.println("Formato incorrecto del mensaje: " + payload);
                                return;
                            }

                            String idSensor = partes[0];
                            String tipoSensor;
                            String lectura = partes[1];

                            // Obtenemos el valor numérico
                            double valor;
                            if (lectura.endsWith("%")) { // Humedad
                                tipoSensor = "Humedad";
                                lectura = lectura.substring(0, lectura.length() - 1); // Remover el '%'
                                valor = Double.parseDouble(lectura);
                            } else if (lectura.endsWith("°C") || lectura.endsWith("°F")) { // Temperatura
                                tipoSensor = "Temperatura";
                                lectura = lectura.substring(0, lectura.length() - 2); // Remover '°C'
                                valor = Double.parseDouble(lectura);
                            } else {
                                System.err.println("Dato no válido: " + lectura);
                                return;
                            }

                            // Obtenemos el timestamp
                            String timestamp = Instant.now().toString();

                            /**
                             * Creamos un objeto MedicionDTO con el id del
                             * sensor, el tipo de sensor, el valor medico y el
                             * timestamp de la lecutura.
                             */
                            MedicionDTO medicion = new MedicionDTO(idSensor, tipoSensor, valor, timestamp);
                            String json = gson.toJson(medicion);

                            // Publicar en RabbitMQ
                            channel.basicPublish("", QUEUE_NAME, null, json.getBytes());
                            System.out.println("Publicado en RabbitMQ: " + json);
                        } catch (IOException e) {
                            System.err.println("Error publicando en RabbitMQ: " + e.getMessage());
                        }
                    });
                }

                // No lo usamos aquí.
                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            });

            // Conectamos el cliente al broker
            client.connect();

            // Indicamos que reciberá todas las mediciones de cual quier tipo de sensor
            client.subscribe("sensores/#");

            System.out.println("Suscrito al topic: sensores/#");
        } catch (MqttException | IOException | TimeoutException ex) {
            Logger.getLogger(Gateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
