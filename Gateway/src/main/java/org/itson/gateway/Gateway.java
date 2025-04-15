/*
 * Gateway.java
 */
package org.itson.gateway;

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

    public static void main(String[] args) {
        try {
            // ----------------- CON MQTT------------------
            // IP donde estará corriendo Mosquitto (localhost si es en local)
            String brokerIP = "localhost";
            // Dirección del broker (en este caso es Mosquitto en local)
            String broker = "tcp://" + brokerIP + ":1883";
            // Generamos un ID de cliente
            String clientId = MqttClient.generateClientId();
            // Ahora creamos un cliente MQTT
            MqttClient client = new MqttClient(broker, clientId);

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
                    System.out.println("Mensaje recibido en " + topic + ": " + message.toString());
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
        } catch (MqttException ex) {
            Logger.getLogger(Gateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
