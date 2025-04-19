package org.itson.GestionSensores.proto;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServidorGrpcLauncher {

    @Autowired
    private ServidorGestionSensoresGRPC servidorGestionSensoresGRPC;

    private Server server;

    @PostConstruct
    public void start() throws Exception {
        server = ServerBuilder.forPort(50051)
                .addService(servidorGestionSensoresGRPC)
                .build()
                .start();

        System.out.println("Servidor gRPC levantado en el puerto 50051");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor gRPC...");
            server.shutdown();
        }));
    }
}
