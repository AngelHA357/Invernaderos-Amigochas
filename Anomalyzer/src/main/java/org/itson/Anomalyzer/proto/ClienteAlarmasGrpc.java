package org.itson.Anomalyzer.proto;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.Alarma.Alarmas;
import org.itson.Alarma.AlarmasServidorGrpc;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ClienteAlarmasGrpc {
    @GrpcClient("alarmas")
    AlarmasServidorGrpc.AlarmasServidorBlockingStub stub;

    public List<Alarmas.AlarmaDTO> obtenerAlarmas() {
        // Crear el mensaje vacío
        Alarmas.Empty request = Alarmas.Empty.newBuilder().build();

        // Llamar al stub gRPC
        Alarmas.AlarmasList response = stub.obtenerAlarmas(request);

        // Regresar la lista de alarmas
        return response.getAlarmasList();
    }
}
