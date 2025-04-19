package org.itson.Lecturas.proto;

import com.itson.grpc.GestionSensoresServicioGrpc;
import com.itson.grpc.SensorPeticion;
import com.itson.grpc.SensorRespuesta;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class ClienteGestionSensoresGRPC {
    private GestionSensoresServicioGrpc.GestionSensoresServicioBlockingStub stub;

    public ClienteGestionSensoresGRPC(@GrpcClient("gestion-sensores") GestionSensoresServicioGrpc.GestionSensoresServicioBlockingStub stub) {
        this.stub = stub;
    }

    public SensorRespuesta obtenerSensor(String macAddress) {
        SensorPeticion request = SensorPeticion.newBuilder().setMacAddress(macAddress).build();
        return stub.getSensorByMac(request);
    }
}
