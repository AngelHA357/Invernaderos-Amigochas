package org.itson.GestionSensores.proto;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.itson.GestionSensores.dtos.DatosFaltantesDTO;
import org.itson.GestionSensores.service.GestionSensoresService;
import org.itson.grpc.gestionInformes.DatosFaltantes;
import org.itson.grpc.gestionInformes.RespuestaDatos;
import org.itson.grpc.gestionInformes.ServicioGestionLecturasGrpc;
import org.itson.grpc.gestionInformes.SolicitudDatos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class ServidorLecturasGrpc extends ServicioGestionLecturasGrpc.ServicioGestionLecturasImplBase {

    @Autowired
    private GestionSensoresService gestionSensoresService;

    @Override
    public void obtenerDatosFaltantes(SolicitudDatos request, StreamObserver<RespuestaDatos> responseObserver) {
        List<String> idSensores = request.getIdSensorList();
        List<DatosFaltantesDTO> listaDatosFaltantes = null;
        try {
            listaDatosFaltantes = gestionSensoresService.obtenerDatosSensores(idSensores);
        } catch (Exception e) {
            // Manejo de excepciones
            responseObserver.onError(new RuntimeException("Error al obtener datos faltantes: " + e.getMessage()));
            return;
        }

        // Convertimos la lista de DTOs al formato de la respuesta gRPC
        RespuestaDatos.Builder respuestaBuilder = RespuestaDatos.newBuilder();

        for (DatosFaltantesDTO dto : listaDatosFaltantes) {
            DatosFaltantes datos = DatosFaltantes.newBuilder()
                    .setIdSensor(dto.getIdSensor())
                    .setSector(dto.getSector())
                    .setFila(dto.getFila())
                    .build();

            respuestaBuilder.addDatosFaltantes(datos);
        }

        // Enviamos la respuesta
        responseObserver.onNext(respuestaBuilder.build());
        responseObserver.onCompleted();
    }
}

