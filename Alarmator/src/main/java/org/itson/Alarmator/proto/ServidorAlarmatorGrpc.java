package org.itson.Alarmator.proto;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.itson.Alarmator.Alarmator;
import org.itson.Alarmator.AlarmatorServidorGrpc;
import org.itson.Alarmator.dtos.AlarmaDTO;
import org.itson.Alarmator.service.AlarmatorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class ServidorAlarmatorGrpc extends AlarmatorServidorGrpc.AlarmatorServidorImplBase {
    @Autowired
    private AlarmatorService alarmatorService;

    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    @Override
    public void registrarAlarma(Alarmator.AlarmaDTO request, StreamObserver<Alarmator.Empty> responseObserver) {
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

        alarmatorService.agregarAlarma(alarmaDTO);

        responseObserver.onNext(Alarmator.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarAlarma(Alarmator.AlarmaDTO request, StreamObserver<Alarmator.Empty> responseObserver) {
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

        alarmatorService.actualizarAlarma(alarmaDTO);

        responseObserver.onNext(Alarmator.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void eliminarAlarma(Alarmator.AlarmaDTO request, StreamObserver<Alarmator.Empty> responseObserver) {
        System.out.println("Eliminando alarma: " + request.getIdAlarma());

        AlarmaDTO alarmaDTO = new AlarmaDTO();
        alarmaDTO.setIdAlarma(request.getIdAlarma());

        alarmatorService.eliminarAlarma(alarmaDTO);

        responseObserver.onNext(Alarmator.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarAlarmas(Alarmator.AlarmasList request, StreamObserver<Alarmator.Empty> responseObserver) {
        System.out.println("Actualizando TODAS las alarmas");

        List<Alarmator.AlarmaDTO> alarmas = request.getAlarmasList();
        List<AlarmaDTO> alarmasDTO = new ArrayList<>();
        for (Alarmator.AlarmaDTO alarma : alarmas) {
            AlarmaDTO alarmaDTO = new AlarmaDTO(
                    alarma.getIdAlarma(),
                    alarma.getIdSensoresList(),
                    alarma.getInvernadero(),
                    alarma.getValorMinimo(),
                    alarma.getValorMaximo(),
                    alarma.getMagnitud(),
                    alarma.getUnidad(),
                    alarma.getMedioNotificacion(),
                    alarma.getActivo()
            );
            alarmasDTO.add(alarmaDTO);
        }

        alarmatorService.actualizarAlarmas(alarmasDTO);

        responseObserver.onNext(Alarmator.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
