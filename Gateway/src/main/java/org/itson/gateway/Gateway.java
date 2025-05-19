/*
 * Gateway.java
 */
package org.itson.gateway;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dtos.LecturaDTO;
import dtos.LecturaEnriquecidaDTO;
import encriptadores.EncriptadorRSA;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
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

/**
 *
 * @author ricar
 */
public class Gateway {

    private static final String QUEUE_NAME = "lecturas";
    private static final String ID_INVERNADERO = "INV-0101";
    private static final String NOMBRE_INVERNADERO = "Invernadero A";

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
                            PrivateKey llavePrivada = EncriptadorRSA.loadPrivateKey("src\\main\\resources\\keys\\clave_privada_gateway.pem");
                            String payload = EncriptadorRSA.decrypt(message.getPayload(), llavePrivada);
                            
                            System.out.println("Mensaje recibido en " + topic + ": " + payload);

                            // Deserializar JSON
                            LecturaDTO lectura = gson.fromJson(payload, LecturaDTO.class);
                            
                            //Se agregan los datos del invernadero a la lectura
                            LecturaEnriquecidaDTO lecturaEnriquecida = new LecturaEnriquecidaDTO();
                            
                            lecturaEnriquecida.setIdInvernadero(ID_INVERNADERO);
                            lecturaEnriquecida.setNombreInvernadero(NOMBRE_INVERNADERO);
                            lecturaEnriquecida.setIdSensor(lectura.getIdSensor());
                            lecturaEnriquecida.setMacAddress(lectura.getMacAddress());
                            lecturaEnriquecida.setMarca(lectura.getMarca());
                        lecturaEnriquecida.setModelo(lectura.getModelo());
                            lecturaEnriquecida.setMagnitud(lectura.getMagnitud());
                            lecturaEnriquecida.setUnidad(lectura.getUnidad());
                            lecturaEnriquecida.setValor(lectura.getValor());
                            lecturaEnriquecida.setFechaHora(lectura.getFechaHora());

                            // Publicar en RabbitMQ
                            String json = gson.toJson(lecturaEnriquecida);

                            PublicKey llavePublica = EncriptadorRSA.loadPublicKey("src\\main\\resources\\keys\\clave_publica_lecturas.pem");

                            byte[] jsonEncriptado = EncriptadorRSA.encrypt(payload, llavePublica);

                            channel.basicPublish("", QUEUE_NAME, null, jsonEncriptado);

                            System.out.println("Mensaje publicado en RabbitMQ con éxito.");
                        } catch (IOException e) {
                            System.err.println("Error publicando en RabbitMQ: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Error al procesar mensaje: " + e.getMessage());
                        }
                    });
                }

                // No lo usamos.
                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                }
            });

            // Conectamos el cliente al broker
            client.connect();

            // Indicamos que reciberá todas las lecturas de cualquier tipo de sensor
            client.subscribe("sensores/lecturas/#");

            System.out.println("Suscrito al topic: sensores/lecturas/#");
        } catch (MqttException | IOException | TimeoutException ex) {
            Logger.getLogger(Gateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
