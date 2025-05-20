package org.itson.ReportesAnomalias.proto;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.ReportesAnomalias.dtos.DatosFaltantesDTO;
import org.itson.grpc.gestionInformes.DatosFaltantes;
import org.itson.grpc.gestionInformes.RespuestaDatos;
import org.itson.grpc.gestionInformes.ServicioGestionLecturasGrpc;
import org.itson.grpc.gestionInformes.SolicitudDatos;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteGestionSensoresGrpc {

    @GrpcClient("gestion-sensores-service")
    private ServicioGestionLecturasGrpc.ServicioGestionLecturasBlockingStub stub;

    /**
     * Llama al servicio gRPC en GestionSensores para enriquecer una lista de lecturas.
     *
     * @param idSensores Lista de IDs de los sensores que se buscan obtener los datos.
     * @return Lista de objetos LecturaEnriquecida (generados por Protobuf), o una lista vacía en caso de error.
     */
    public List<DatosFaltantesDTO> obtenerDatosFaltantes(List<String> idSensores) {
        SolicitudDatos solicitud = SolicitudDatos.newBuilder()
                .addAllIdSensor(idSensores)
                .build();
        RespuestaDatos respuestaDatos = stub.obtenerDatosFaltantes(solicitud);
        List<DatosFaltantes> datosFaltantesList = respuestaDatos.getDatosFaltantesList();
        List<DatosFaltantesDTO> listaDatosFaltantesDTO = new ArrayList<>();
        for (DatosFaltantes datosFaltantes : datosFaltantesList) {
            DatosFaltantesDTO datosFaltantesDTO = new DatosFaltantesDTO(
                    datosFaltantes.getIdSensor(),
                    datosFaltantes.getSector(),
                    datosFaltantes.getFila()
            );
            listaDatosFaltantesDTO.add(datosFaltantesDTO);
        }
        return listaDatosFaltantesDTO;
    }
}
