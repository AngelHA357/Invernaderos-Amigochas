/*
 * Gateway.java
 */
package org.itson.gateway;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricar
 */
public class Gateway {
    
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(1001);
            
            Executor service = Executors.newCachedThreadPool();

            while(true) {
                service.execute(new ProtocoloGateway(socket));
            }
        } catch (SocketException ex) {
            Logger.getLogger(Gateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
