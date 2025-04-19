package org.itson.Lecturas.proto;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.grpc.GestionSensoresServidorGrpc;
import org.itson.grpc.SensorLectura;
import org.itson.grpc.SensorPeticion;
import org.itson.grpc.SensorRespuesta;
import org.springframework.stereotype.Component;

@Component
public class ClienteGestionSensoresGRPC {
    private GestionSensoresServidorGrpc.GestionSensoresServidorBlockingStub stub;

    public ClienteGestionSensoresGRPC(@GrpcClient("gestion-sensores")GestionSensoresServidorGrpc.GestionSensoresServidorBlockingStub stub) {
        this.stub = stub;
    }

    public SensorRespuesta obtenerSensor(SensorLectura sensor) {
        SensorPeticion request = SensorPeticion.newBuilder().setSensorLectura(sensor).build();
        return stub.getSensor(request);
    }
}