package org.itson.Alarmas.proto;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.bson.types.ObjectId;
import org.itson.Alarmas.dtos.AlarmaDTO;
import org.itson.Anomalyzer.Anomalyzer;
import org.itson.Anomalyzer.AnomalyzerServidorGrpc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteAnomalyzerGrpc {
    private AnomalyzerServidorGrpc.AnomalyzerServidorBlockingStub stub;

    public ClienteAnomalyzerGrpc(@GrpcClient("anomalyzer") AnomalyzerServidorGrpc.AnomalyzerServidorBlockingStub stub) {
        this.stub = stub;
    }

    public void registrarAlarma(AlarmaDTO alarmaDTO) {
        Anomalyzer.AlarmaDTO alarmaNueva = Anomalyzer.AlarmaDTO.newBuilder()
                .setIdAlarma(alarmaDTO.getIdAlarma())
                .addAllIdSensores(alarmaDTO.getSensores())
                .setInvernadero(alarmaDTO.getInvernadero())
                .setValorMinimo(alarmaDTO.getValorMinimo())
                .setValorMaximo(alarmaDTO.getValorMaximo())
                .setMagnitud(alarmaDTO.getMagnitud())
                .setUnidad(alarmaDTO.getUnidad())
                .setMedioNotificacion(alarmaDTO.getMedioNotificacion())
                .setActivo(alarmaDTO.isActivo())
                .build();

        stub.registrarAlarma(alarmaNueva);
        System.out.println("Alarma registrada correctamente.");
    }

    public void actualizarAlarma(AlarmaDTO alarmaDTO) {
        Anomalyzer.AlarmaDTO alarmaNueva = Anomalyzer.AlarmaDTO.newBuilder()
                .setIdAlarma(alarmaDTO.getIdAlarma())
                .addAllIdSensores(alarmaDTO.getSensores())
                .setInvernadero(alarmaDTO.getInvernadero())
                .setValorMinimo(alarmaDTO.getValorMinimo())
                .setValorMaximo(alarmaDTO.getValorMaximo())
                .setMagnitud(alarmaDTO.getMagnitud())
                .setUnidad(alarmaDTO.getUnidad())
                .setMedioNotificacion(alarmaDTO.getMedioNotificacion())
                .setActivo(alarmaDTO.isActivo())
                .build();

        stub.actualizarAlarma(alarmaNueva);
        System.out.println("Alarma registrada correctamente.");
    }

    public void eliminarAlarma(String idAlarma) {
        Anomalyzer.AlarmaDTO alarmaDTO = Anomalyzer.AlarmaDTO
                .newBuilder()
                .setIdAlarma(idAlarma)
                .build();

        stub.eliminarAlarma(alarmaDTO);
        System.out.println("Alarma registrada correctamente.");
    }

    public void actualizarAlarmas(List<AlarmaDTO> alarmas) {
        List<Anomalyzer.AlarmaDTO> alarmasGrpc = new ArrayList<>();
        for (AlarmaDTO alarma : alarmas) {
            alarmasGrpc.add(
                    Anomalyzer.AlarmaDTO.newBuilder()
                            .setIdAlarma(alarma.getIdAlarma())
                            .addAllIdSensores(alarma.getSensores())
                            .setInvernadero(alarma.getInvernadero())
                            .setValorMinimo(alarma.getValorMinimo())
                            .setValorMaximo(alarma.getValorMaximo())
                            .setMagnitud(alarma.getMagnitud())
                            .setUnidad(alarma.getUnidad())
                            .setMedioNotificacion(alarma.getMedioNotificacion())
                            .setActivo(alarma.isActivo())
                            .build()
            );
        }
        Anomalyzer.AlarmasList alarmasList = Anomalyzer.AlarmasList.newBuilder()
                .addAllAlarmas(alarmasGrpc)
                .build();

        stub.actualizarAlarmas(alarmasList);
    }
}