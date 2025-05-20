package org.itson.GestionSensores.proto;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.service.GestionSensoresService;
import org.itson.grpc.gestioninformes.LecturaEnriquecida;
import org.itson.grpc.gestioninformes.LecturaOriginal;
import org.itson.grpc.gestioninformes.RespuestaEnriquecimiento;
import org.itson.grpc.gestioninformes.ServicioGestionLecturasGrpc;
import org.itson.grpc.gestioninformes.SolicitudEnriquecimiento;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@GrpcService
public class ServidorLecturasGrpc extends ServicioGestionLecturasGrpc.ServicioGestionLecturasImplBase {

    @Autowired
    private GestionSensoresService gestionSensoresService;

    @Override
    public void enriquecerLecturas(SolicitudEnriquecimiento request, StreamObserver<RespuestaEnriquecimiento> responseObserver) {
        System.out.println("Servicio GestionSensores: Solicitud de enriquecimiento recibida para " + request.getLecturasCount() + " lecturas.");
        List<LecturaEnriquecida> lecturasEnriquecidasList = new ArrayList<>();

        for (LecturaOriginal lecturaOriginalProto : request.getLecturasList()) {
            try {
                SensorDTO sensorDetalles = gestionSensoresService.obtenerSensorPorId(lecturaOriginalProto.getIdSensor());

                LecturaEnriquecida.Builder lecturaEnriquecidaBuilder = LecturaEnriquecida.newBuilder()
                        .setIdLectura(lecturaOriginalProto.getIdLectura())
                        .setIdSensor(lecturaOriginalProto.getIdSensor())
                        .setMagnitud(lecturaOriginalProto.getMagnitud())
                        .setValor(lecturaOriginalProto.getValor())
                        .setFechaHora(lecturaOriginalProto.getFechaHora())
                        .setIdInvernadero(lecturaOriginalProto.getIdInvernadero());

                if (sensorDetalles.getSector() != null) {
                    lecturaEnriquecidaBuilder.setSector(sensorDetalles.getSector());
                }
                if (sensorDetalles.getFila() != null) {
                    lecturaEnriquecidaBuilder.setFila(sensorDetalles.getFila());
                }

                lecturasEnriquecidasList.add(lecturaEnriquecidaBuilder.build());

            } catch (GestionSensoresException e) {
                System.err.println("Error al enriquecer lectura para sensor ID " + lecturaOriginalProto.getIdSensor() + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error inesperado al enriquecer lectura para sensor ID " + lecturaOriginalProto.getIdSensor() + ": " + e.getMessage());
            }
        }

        RespuestaEnriquecimiento respuesta = RespuestaEnriquecimiento.newBuilder()
                .addAllLecturasEnriquecidas(lecturasEnriquecidasList)
                .build();

        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
        System.out.println("Servicio GestionSensores: Respuesta de enriquecimiento enviada con " + lecturasEnriquecidasList.size() + " lecturas.");
    }

    private Timestamp convertDateToTimestamp(Date date) {
        if (date == null) {
            return Timestamp.newBuilder().build(); // O manejar como error
        }
        Instant instant = date.toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
