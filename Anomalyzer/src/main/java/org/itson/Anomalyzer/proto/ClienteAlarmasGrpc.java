package org.itson.Anomalyzer.proto;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.alarma.Alarmas;
import org.itson.alarma.AlarmasServidorGrpc;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClienteAlarmasGrpc {
    @GrpcClient("alarmas")
    AlarmasServidorGrpc.AlarmasServidorBlockingStub stub;

    public void desactivarAlarma(AlarmaDTO alarma) {
        // Crear el mensaje con el ID de la alarma
        Alarmas.AlarmaDTO request = Alarmas.AlarmaDTO.newBuilder().
                setIdAlarma(alarma.getIdAlarma()).
                addAllIdSensores(alarma.getSensores()).
                setInvernadero(alarma.getInvernadero()).
                setValorMinimo(alarma.getValorMinimo()).
                setValorMaximo(alarma.getValorMaximo()).
                setMagnitud(alarma.getMagnitud()).
                setUnidad(alarma.getUnidad()).
                setMedioNotificacion(alarma.getMedioNotificacion()).
                build();

        // Llamar al stub gRPC
        stub.desactivarAlarma(request);
    }
}
