package org.itson.Alarmas.proto;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.Alarmas.dtos.AlarmaDTO;
import org.itson.anomalyzerSever.Anomalyzer;
import org.itson.anomalyzerSever.AnomalyzerServidorGrpc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteAnomalyzerGrpc {
    @GrpcClient("anomalyzer")
    AnomalyzerServidorGrpc.AnomalyzerServidorBlockingStub stub;

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
        System.out.println("Alarma actualizada correctamente.");
    }

    public void eliminarAlarma(String idAlarma) {
        Anomalyzer.AlarmaDTO alarmaDTO = Anomalyzer.AlarmaDTO
                .newBuilder()
                .setIdAlarma(idAlarma)
                .build();

        stub.eliminarAlarma(alarmaDTO);
        System.out.println("Alarma eliminada correctamente.");
    }
}