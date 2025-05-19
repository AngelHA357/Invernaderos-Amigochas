package org.itson.Alarmas.proto;


import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.itson.Alarmas.dtos.AlarmaDTO;
import org.itson.Alarmas.service.AlarmasService;
import org.itson.alarma.Alarmas;
import org.itson.alarma.AlarmasServidorGrpc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
@Slf4j
public class ServidorAlarmasGrpc extends AlarmasServidorGrpc.AlarmasServidorImplBase {
    @Autowired
    private AlarmasService alarmasService;

    @Override
    public void desactivarAlarma(Alarmas.AlarmaDTO request, StreamObserver<Alarmas.Empty> responseObserver) {
        // Convertimos de request DTO a DTO normalita
        AlarmaDTO alarmaDTO = new AlarmaDTO(
                request.getIdAlarma(),
                request.getMagnitud(),
                request.getIdSensoresList(),
                request.getInvernadero(),
                request.getValorMinimo(),
                request.getValorMaximo(),
                request.getUnidad(),
                request.getMedioNotificacion(),
                false);
        // Desactivamos la alarma
        alarmasService.editarAlarma(alarmaDTO);

        responseObserver.onNext(Alarmas.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}