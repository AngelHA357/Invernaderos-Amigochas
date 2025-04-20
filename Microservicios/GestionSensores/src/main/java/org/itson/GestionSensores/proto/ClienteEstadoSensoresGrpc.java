package org.itson.GestionSensores.proto;

import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.grpc.EstadoPeticion;
import org.itson.grpc.EstadoRespuesta;
import org.itson.grpc.EstadoSensoresServidorGrpc;
import org.springframework.stereotype.Component;

@Component
public class ClienteEstadoSensoresGrpc {
    private EstadoSensoresServidorGrpc.EstadoSensoresServidorBlockingStub stub;

    public ClienteEstadoSensoresGrpc(@GrpcClient("estado-sensores") EstadoSensoresServidorGrpc.EstadoSensoresServidorBlockingStub stub) {
        this.stub = stub;
    }

    public void actualizarEstados() {
        stub.actualizarEstados(Empty.newBuilder().build());
    }
}