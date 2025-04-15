/*
 * SensorTemperatura.java
 */
package org.itson.simuladorsensores.sensores;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author Equipo1
 */
public class SensorTemperatura extends Sensor implements Runnable {

    private Random random;
    private long contador;
    private Float dato;
    private MqttClient client;
    private String topic;

    public SensorTemperatura(String id, String marca, String modelo, MqttClient client) {
        super(id, marca, modelo);
        this.client = client;
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        contador = 0;
        random = new Random();
        // Tópico donde se publicará la información del sensor
        topic = "sensores/temperatura";
    }

    @Override
    public void run() {
        try {
            if (contador <= 1000 || contador >= 1100) {
                //Se genera un dato de temperatura entre 18°C y 24°C
                dato = random.nextFloat(18.0f, 24.0f);
            } else {
                //Una vez que se mandaron 1000 datos, manda 100 datos anómalos
                dato = random.nextFloat(24.0f, 35.0f);
                contador = 0;
            }

            // El mensaje a enviar es el id del sensor y la medición
            String payload = id + "/" + String.valueOf(dato);
            // Se obtienen los bytes del payload
            MqttMessage message = new MqttMessage(payload.getBytes());

            /**
             * Establecemos la calidad del servicio con 0 para indicar que el
             * mensaje se envía una sola vez sin necesidad de confirmar nada ni
             * ACKS.
             */
            message.setQos(0);

            // Publicamos el mensaje con el tópico correspondiente.
            client.publish(topic, message);
            System.out.println(contador + " - " + "Mensaje publicado: " + payload + "°C");

            contador++;
        } catch (MqttException ex) {
            Logger.getLogger(SensorTemperatura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
