package org.itson.Anomalyzer.proto;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.service.Analizador;
import org.itson.anomalyzerSever.*;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class ServidorAnomalyzerGrpc extends AnomalyzerServidorGrpc.AnomalyzerServidorImplBase {
    @Autowired
    private Analizador analizador;

    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    @Override
    public void registrarAlarma(Anomalyzer.AlarmaDTO request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Registrando alarma: " + request.getIdAlarma());
        AlarmaDTO alarmaDTO = new AlarmaDTO(
                request.getIdAlarma(),
                request.getIdSensoresList(),
                request.getInvernadero(),
                request.getValorMinimo(),
                request.getValorMaximo(),
                request.getMagnitud(),
                request.getUnidad(),
                request.getMedioNotificacion(),
                request.getActivo()
        );

        analizador.agregarAlarma(alarmaDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarAlarma(Anomalyzer.AlarmaDTO request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Actualizando alarma: " + request.getIdAlarma());
        AlarmaDTO alarmaDTO = new AlarmaDTO(
                request.getIdAlarma(),
                request.getIdSensoresList(),
                request.getInvernadero(),
                request.getValorMinimo(),
                request.getValorMaximo(),
                request.getMagnitud(),
                request.getUnidad(),
                request.getMedioNotificacion(),
                request.getActivo()
        );

        analizador.actualizarAlarma(alarmaDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void eliminarAlarma(Anomalyzer.AlarmaDTO request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Eliminando alarma: " + request.getIdAlarma());

        AlarmaDTO alarmaDTO = new AlarmaDTO();
        alarmaDTO.setIdAlarma(request.getIdAlarma());

        analizador.eliminarAlarma(alarmaDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
