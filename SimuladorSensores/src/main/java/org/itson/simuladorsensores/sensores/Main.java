/*
 * Main.java
 */
package org.itson.simuladorsensores.sensores;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Equipo1
 */
public class Main {

    public static void main(String[] args) {
        Scanner tec = new Scanner(System.in);
        String tipoSensor;
        Sensor sensor = null;

        System.out.print("Indique el tipo de sensor que desea iniciar (Humedad / Temperatura): ");
        tipoSensor = tec.nextLine();
        do {
            if (!tipoSensor.equalsIgnoreCase("Humedad") && !tipoSensor.equalsIgnoreCase("Temperatura")) {
                System.out.print("Ingrese un tipo de sensor valido (Humedad / Temperatura): ");
                tipoSensor = tec.nextLine();
            }
        } while (!tipoSensor.equalsIgnoreCase("Humedad") && !tipoSensor.equalsIgnoreCase("Temperatura"));

        if (tipoSensor.equalsIgnoreCase("Humedad")) {
            sensor = new SensorHumedad();
        } else if (tipoSensor.equalsIgnoreCase("Temperatura")) {
            sensor = new SensorTemperatura();
        }

        Executor service = Executors.newCachedThreadPool();

        while (true) {
            service.execute(sensor);
        }
    }

}
