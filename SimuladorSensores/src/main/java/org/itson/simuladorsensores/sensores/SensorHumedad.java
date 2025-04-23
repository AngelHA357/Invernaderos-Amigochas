package org.itson.simuladorsensores.sensores;

import com.google.gson.Gson;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.itson.simuladorsensores.utils.DTOUtils.LecturaDTO;

public class SensorHumedad extends Sensor implements Runnable {

    private Random random;
    private long contador;
    private MqttClient client;
    private String topic;
    private Gson gson;

    public SensorHumedad(String id, String macAddress, String marca, String modelo, MqttClient client) {
        super(id, macAddress, marca, modelo);
        this.client = client;
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        contador = 0;
        random = new Random();
        topic = "sensores/lecturas/humedad";
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            valor = random.nextFloat(78.0f, 100.0f);

            // Construimos el DTO
            LecturaDTO lectura = new LecturaDTO(
                    idSensor,
                    macAddress,
                    marca,
                    modelo,
                    "Humedad",
                    "%",
                    valor,
                    new Date()
            );

            // Convertimos a JSON
            String payload = gson.toJson(lectura);

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(0);
            client.publish(topic, message);

            System.out.println(contador + " - DTO publicado: " + payload);
            contador++;
        } catch (MqttException ex) {
            Logger.getLogger(SensorHumedad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
