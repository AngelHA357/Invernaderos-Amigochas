/*
 * ProtocoloGateway.java
 */
package org.itson.gateway;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Equipo1
 */
public class ProtocoloGateway implements Runnable {
    
    private DatagramSocket socket;
    
    public ProtocoloGateway(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(1001);
            byte[] buffer = new byte[4096];
            
            while (true) {
                DatagramPacket datoPacket = new DatagramPacket(buffer, buffer.length);

                //Se obtiene el dato enviado por el sensor
                socket.receive(datoPacket);
                String informacion = new String(datoPacket.getData(), 0, datoPacket.getLength());
                String[] datos = informacion.trim().split("/");

                System.out.println("Sensor " + datos[0] + ": " + datos[1]);
            }
        } catch (IOException ex) {
            Logger.getLogger(ProtocoloGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
