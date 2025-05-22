package org.itson.Simulador.sensores;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.itson.Simulador.dtos.LecturaDTO;
import org.itson.Simulador.encriptadores.EncriptadorRSA;

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
            valor = random.nextFloat(26.0f, 47.0f);

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

            // Abrimos el InputStream desde recursos en el classpath
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/clave_publica_gateway.pem")) {
                if (is == null) {
                    throw new FileNotFoundException("No se encontró la llave pública en recursos");
                }
                PublicKey llavePublica = EncriptadorRSA.loadPublicKey(is);

                byte[] payloadEncriptado = EncriptadorRSA.encrypt(payload, llavePublica);

                MqttMessage message = new MqttMessage(payloadEncriptado);
                message.setQos(0);
                client.publish(topic, message);

                System.out.println(contador + " - DTO publicado: " + payload);
                contador++;
            }
        } catch (MqttException ex) {
            Logger.getLogger(SensorTemperatura.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SensorTemperatura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
