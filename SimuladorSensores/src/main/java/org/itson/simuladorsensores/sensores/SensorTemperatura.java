/*
 * SensorTemperatura.java
 */
package org.itson.simuladorsensores.sensores;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Equipo1
 */
public class SensorTemperatura extends Sensor implements Runnable {

    private DatagramSocket socket;
    private Random random;
    private long contador;
    private Float dato;

    public SensorTemperatura(String id, String marca, String modelo) {
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
                //Se genera un dato de temperatura entre 18°C y 24°C
                dato = random.nextFloat(18.0f, 24.0f);
                contador++;

                //Una vez que se mandaron 1000 datos, manda un dato anómalo
                if (contador == 1000) {
                    dato = random.nextFloat(10.0f, 30.0f);
                    contador = 0;
                }

                String informacion = id + "/" + dato.toString();
                byte[] bytesToSend = informacion.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getLocalHost(), 1001);
                socket.send(sendPacket);

                //Espera 1 segundo para mandar el siguiente dato
                Thread.sleep(Duration.ofSeconds(1));
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(SensorTemperatura.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
