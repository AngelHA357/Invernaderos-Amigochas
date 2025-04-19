/*
 * Main.java
 */
package org.itson.simuladorsensores.sensores;

import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * @author Equipo1
 */
public class Main {

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
            // Para la entrada de datos del usuario
            Scanner tec = new Scanner(System.in);

            // Conectamos el cliente al servidor
            client.connect();

            // Le preguntamos al usuario qué tipo de sensor quiere usar
            String tipoSensor;
            System.out.print("Indique el tipo de sensor que desea iniciar (Humedad / Temperatura): ");
            do {
                tipoSensor = tec.nextLine();
                if (!tipoSensor.equalsIgnoreCase("Humedad") && !tipoSensor.equalsIgnoreCase("Temperatura")) {
                    System.out.print("Ingrese un tipo de sensor - Humedad / Temperatura: ");
                }
            } while (!tipoSensor.equalsIgnoreCase("Humedad") && !tipoSensor.equalsIgnoreCase("Temperatura"));

            String magnitud = "";
            if (tipoSensor.equalsIgnoreCase("Temperatura")) {
                System.out.print("Indique la magnitud de la temperatura - C (Celsius) / F (Fahrenheit) / K (Kelvin): ");
                do {
                    magnitud = tec.nextLine();
                    if (!magnitud.equalsIgnoreCase("C") && !magnitud.equalsIgnoreCase("F") && !magnitud.equalsIgnoreCase("K")) {
                        System.out.print("Indique la magnitud de la temperatura - C (Celsius) / F (Fahrenheit) / K (Kelvin): ");
                        magnitud = tec.nextLine();
                    }
                } while (!magnitud.equalsIgnoreCase("C") && !magnitud.equalsIgnoreCase("F") && !magnitud.equalsIgnoreCase("K"));
            }

            // Creamos un sensor dependiendo del seleccionado
            Sensor sensor = null;
            if (tipoSensor.equalsIgnoreCase("Humedad")) {
                sensor = new SensorHumedad("SEN-0101", "AA:BB:CC:DD:EE:FF", "SensorTech", "S_MOIST-ST-100", client);
            } else if (tipoSensor.equalsIgnoreCase("Temperatura")) {
                sensor = new SensorTemperatura("SEN-0102", "AA:11:BB:22:CC:33", "TempTech", "T-300", magnitud, client);
            }

            // Creamos un servicio
            Executor service = Executors.newCachedThreadPool();

            while (true) {
                // Mandamos las lecturas del sensor infinitamente :D
                service.execute(sensor);
                Thread.sleep(2000);
            }
        } catch (InterruptedException | MqttException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
