/*
 * Sensor.java
 */
package org.itson.simuladorsensores.sensores;

/**
 * @author Equipo1
 */
public class Sensor implements Runnable {

    protected String idSensor;
    protected String macAddress;
    protected String marca;
    protected String modelo;
    protected Float valor;

    public Sensor(String idSensor, String macAddress, String marca, String modelo) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;

    }

    @Override
    public void run() {
    }
}
