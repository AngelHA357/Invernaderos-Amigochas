/*
 * Sensor.java
 */
package org.itson.simuladorsensores.sensores;

/**
 * @author Equipo1
 */
public class Sensor implements Runnable {

    protected String id;
    protected String marca;
    protected String modelo;

    public Sensor(String id, String marca, String modelo) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
    }

    @Override
    public void run() {
    }
}
