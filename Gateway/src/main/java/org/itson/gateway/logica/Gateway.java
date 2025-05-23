package org.itson.Gateway.logica;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.itson.Gateway.dtos.LecturaDTO;
import org.itson.Gateway.dtos.LecturaEnriquecidaDTO;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.itson.Gateway.encriptadores.EncriptadorRSA;

/**
 * @author ricar
 */
@Component
public class Gateway {

    private static final String QUEUE_NAME = "lecturas";
    private static final String ID_INVERNADERO = "INV-0101";
    private static final String NOMBRE_INVERNADERO = "Invernadero A";

    @PostConstruct
    public void iniciar() {

        try {
            // Mosquitto/MQTT
            String brokerIP = "mosquitto"; // IP de Mosquitto (localhost si es en local)
            String broker = "tcp://" + brokerIP + ":1883"; // URI del servidor Mosquitto
            String clientId = MqttClient.generateClientId(); // ID del cliente
            MqttClient client = new MqttClient(broker, clientId, null);

            // RabbitMQ
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("rabbitmq");
            factory.setPort(5672);
            factory.setUsername("user");
            factory.setPassword("password");

            Connection rabbitConnection = factory.newConnection();
            Channel channel = rabbitConnection.createChannel();
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
                 * @param topic   El topic del mensaje.
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
                            InputStream isPrivateKey = getClass().getClassLoader().getResourceAsStream("keys/clave_privada_gateway.pem");
                            if (isPrivateKey == null) {
                                throw new FileNotFoundException("No se encontró la clave privada en recursos");
                            }
                            PrivateKey llavePrivada = EncriptadorRSA.loadPrivateKey(isPrivateKey);

                            String payload = EncriptadorRSA.decrypt(message.getPayload(), llavePrivada);

                            System.out.println("Mensaje recibido en " + topic + ": " + payload);

                            // Deserializar JSON
                            LecturaDTO lectura = gson.fromJson(payload, LecturaDTO.class);

                            // Se agregan los datos del invernadero a la lectura
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

                            // Abrimos el InputStream desde recursos en el classpath
                            try (InputStream isPublicKey = getClass().getClassLoader().getResourceAsStream("keys/clave_publica_lecturas.pem")) {
                                if (isPublicKey == null) {
                                    throw new FileNotFoundException("No se encontró la llave pública en recursos");
                                }
                                PublicKey llavePublica = EncriptadorRSA.loadPublicKey(isPublicKey);


                                String jsonEncriptado = EncriptadorRSA.encryptHybrid(json, llavePublica);

                                channel.basicPublish("", QUEUE_NAME, null, jsonEncriptado.getBytes());

                                System.out.println("Mensaje publicado en RabbitMQ con éxito.");
                            }

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
