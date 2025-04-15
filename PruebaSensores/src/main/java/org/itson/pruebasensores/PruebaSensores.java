/*
 * PruebaSensores.java
 */
package org.itson.pruebasensores;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Equipo1
 */
public class PruebaSensores {

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(1001);
            byte[] buffer = new byte[4096];
            
            while (true) {
                DatagramPacket datoPacket = new DatagramPacket(buffer, buffer.length);

                //Se obtiene el dato enviado por el sensor
                socket.receive(datoPacket);
                String dato = new String(datoPacket.getData(), 0, datoPacket.getLength());

                System.out.println(dato);
            }
        } catch (IOException ex) {
            Logger.getLogger(PruebaSensores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
