package org.itson.Informes.proto;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.grpc.gestioninformes.LecturaEnriquecida;
import org.itson.grpc.gestioninformes.LecturaOriginal;
import org.itson.grpc.gestioninformes.RespuestaEnriquecimiento;
import org.itson.grpc.gestioninformes.ServicioGestionLecturasGrpc;
import org.itson.grpc.gestioninformes.SolicitudEnriquecimiento;
import org.springframework.stereotype.Component;
import io.grpc.StatusRuntimeException;

import java.util.Collections;
import java.util.List;

@Component
public class ClienteGestionSensoresGrpc {

    @GrpcClient("gestion-sensores-service")
    private ServicioGestionLecturasGrpc.ServicioGestionLecturasBlockingStub blockingStub;

    /**
     * Llama al servicio gRPC en GestionSensores para enriquecer una lista de lecturas.
     *
     * @param lecturasOriginalesProto Lista de objetos LecturaOriginal (generados por Protobuf).
     * @return Lista de objetos LecturaEnriquecida (generados por Protobuf), o una lista vacía en caso de error.
     */
    public List<LecturaEnriquecida> enriquecerLecturasConDetallesSensor(List<LecturaOriginal> lecturasOriginalesProto) {
        if (lecturasOriginalesProto == null || lecturasOriginalesProto.isEmpty()) {
            System.out.println("ClienteGestionSensoresGrpc: No hay lecturas para enriquecer.");
            return Collections.emptyList();
        }

        System.out.println("ClienteGestionSensoresGrpc: Enviando " + lecturasOriginalesProto.size() + " lecturas para enriquecimiento...");

        SolicitudEnriquecimiento solicitud = SolicitudEnriquecimiento.newBuilder()
                .addAllLecturas(lecturasOriginalesProto)
                .build();

        try {
            RespuestaEnriquecimiento respuesta = blockingStub.enriquecerLecturas(solicitud);
            System.out.println("ClienteGestionSensoresGrpc: Respuesta recibida con " + respuesta.getLecturasEnriquecidasCount() + " lecturas enriquecidas.");
            return respuesta.getLecturasEnriquecidasList();
        } catch (StatusRuntimeException e) {
            System.err.println("ClienteGestionSensoresGrpc: Error en la llamada RPC a GestionSensores: " + e.getStatus().toString());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("ClienteGestionSensoresGrpc: Error inesperado al llamar a GestionSensores: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
