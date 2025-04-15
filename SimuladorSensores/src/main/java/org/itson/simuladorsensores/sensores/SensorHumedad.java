/*
 * SensorHumedad.java
 */
package org.itson.simuladorsensores.sensores;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Equipo1
 */
public class SensorHumedad extends Sensor implements Runnable {

    private DatagramSocket socket;
    private Random random;
    private long contador;
    private Float dato;

    public SensorHumedad(String id, String marca, String modelo) {
        super(id, marca, modelo);
        
        inicializarAtributos();
    }
    
    private void inicializarAtributos() {
        try {
            contador = 0;
            socket = new DatagramSocket();
            random = new Random();
        } catch (SocketException ex) {
            Logger.getLogger(SensorHumedad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Se genera un dato de humedad entre 78% y 82%
                dato = random.nextFloat(78.0f, 82.0f);
                contador++;

                //Una vez que se mandaron 1000 datos, manda un dato anómalo
                if (contador == 1000) {
                    dato = random.nextFloat(60.0f, 90.0f);
                    contador = 0;
                }

                String informacion = id + "/" + dato.toString();
                byte[] bytesToSend = informacion.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getLocalHost(), 1001);
                socket.send(sendPacket);

                //Espera 1 segundo para mandar el siguiente dato
                Thread.sleep(2000);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(SensorHumedad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
