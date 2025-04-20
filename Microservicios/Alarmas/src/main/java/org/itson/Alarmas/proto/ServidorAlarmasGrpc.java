package org.itson.Alarmas.proto;


import io.grpc.stub.StreamObserver;
import org.bson.types.ObjectId;
import org.itson.Alarma.Alarmas;
import org.itson.Alarma.AlarmasServidorGrpc;
import org.itson.Alarmas.dtos.AlarmaDTO;
import org.itson.Alarmas.service.AlarmasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServidorAlarmasGrpc extends AlarmasServidorGrpc.AlarmasServidorImplBase {
    @Autowired
    private AlarmasService alarmasService;

    @Override
    public void obtenerAlarmas(Alarmas.Empty request, StreamObserver<Alarmas.AlarmasList> responseObserver) {
        List<AlarmaDTO> listaAlarmas = alarmasService.obtenerTodasLasAlarmas(); // Tu lista de POJOs

        Alarmas.AlarmasList.Builder responseBuilder = Alarmas.AlarmasList.newBuilder();

        for (AlarmaDTO alarmaPojo : listaAlarmas) {
            // Convertir List<ObjectId> a List<String>
            List<String> ids = new ArrayList<>();
            for (ObjectId id : alarmaPojo.getSensores()) {
                ids.add(id.toString());
            }

            // Construir el mensaje gRPC
            Alarmas.AlarmaDTO grpcAlarma = Alarmas.AlarmaDTO.newBuilder()
                    .setIdAlarma(alarmaPojo.getIdAlarma())
                    .setMagnitud(alarmaPojo.getMagnitud())
                    .addAllIdSensores(ids)
                    .setInvernadero(alarmaPojo.getInvernadero())
                    .setValorMinimo(alarmaPojo.getValorMinimo())
                    .setValorMaximo(alarmaPojo.getValorMaximo())
                    .setUnidad(alarmaPojo.getUnidad())
                    .setMedioNotificacion(alarmaPojo.getMedioNotificacion())
                    .setActivo(alarmaPojo.isActivo())
                    .build();

            // Agregar a la lista de respuesta
            responseBuilder.addAlarmas(grpcAlarma);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}