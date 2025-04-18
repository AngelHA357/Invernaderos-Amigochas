package org.itson.simuladorsensores.sensores;

import com.google.gson.Gson;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.itson.dtos.LecturaDTO;

public class SensorTemperatura extends Sensor implements Runnable {

    private String magnitud;
    private Random random;
    private long contador;
    private MqttClient client;
    private String topic;
    private Gson gson;

    public SensorTemperatura(String id, String macAddress, String marca, String modelo, String magnitud, MqttClient client) {
        super(id, macAddress, marca, modelo);
        this.magnitud = magnitud;
        this.client = client;
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        contador = 0;
        random = new Random();
        topic = "sensores/lecturas/temperatura";
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            valor = random.nextFloat(18.0f, 35.0f);

            // Creamos el DTO
            LecturaDTO lectura = new LecturaDTO(
                    idSensor,
                    macAddress,
                    marca,
                    modelo,
                    "Temperatura",
                    magnitud,
                    valor,
                    new Date()
            );

            // Serializamos a JSON
            String payload = gson.toJson(lectura);

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(0);
            client.publish(topic, message);

            System.out.println(contador + " - DTO publicado: " + payload);
            contador++;
        } catch (MqttException ex) {
            Logger.getLogger(SensorTemperatura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
