package org.itson.Anomalyzer.proto;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.Alarma.Alarmas;
import org.itson.Alarma.AlarmasServidorGrpc;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteAlarmasGrpc {
    private AlarmasServidorGrpc.AlarmasServidorBlockingStub stub;

    /**
     * Constructor que inyecta el cliente gRPC.
     *
     * @param stub Cliente gRPC para comunicarse con el servidor de alarmas.
     */
    public ClienteAlarmasGrpc(@GrpcClient("alarmas") AlarmasServidorGrpc.AlarmasServidorBlockingStub stub) {
        this.stub = stub;
    }

    public List<Alarmas.AlarmaDTO> obtenerAlarmas() {
        // Crear el mensaje vacío
        Alarmas.Empty request = Alarmas.Empty.newBuilder().build();

        // Llamar al stub gRPC
        Alarmas.AlarmasList response = stub.obtenerAlarmas(request);

        // Regresar la lista de alarmas
        return response.getAlarmasList();
    }
}
