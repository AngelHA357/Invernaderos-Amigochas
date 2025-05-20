package org.itson.Informes.service;

import com.google.protobuf.Timestamp;
import org.bson.types.ObjectId;
import org.itson.Informes.dtos.InformeResponseDTO;
import org.itson.Informes.dtos.LecturaDTO;
import org.itson.Informes.collections.LecturaCruda;
import org.itson.Informes.proto.ClienteGestionSensoresGrpc;
import org.itson.grpc.gestioninformes.LecturaEnriquecida;
import org.itson.grpc.gestioninformes.LecturaOriginal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformeService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ClienteGestionSensoresGrpc clienteGestionSensores; // Nuestro cliente gRPC

    /**
     * Genera un informe obteniendo lecturas crudas de la colección principal,
     * enriqueciéndolas vía gRPC con GestionSensores, y devolviendo un DTO para el frontend.
     *
     * @param idsInvernaderoFiltro Lista de IDs de invernadero para filtrar las lecturas.
     * @param fechaInicioFiltro    Fecha de inicio para el filtro de lecturas.
     * @param fechaFinFiltro       Fecha de fin para el filtro de lecturas.
     * @param magnitudesFiltro     Lista de magnitudes para filtrar las lecturas.
     * @return InformeResponseDTO que contiene los detalles del informe y las lecturas enriquecidas.
     * @throws IllegalArgumentException Si los parámetros de filtro son inválidos.
     * @throws RuntimeException Si ocurre un error durante la generación.
     */
    public InformeResponseDTO generarInformeParaFrontend(
            List<String> idsInvernaderoFiltro,
            Date fechaInicioFiltro,
            Date fechaFinFiltro,
            List<String> magnitudesFiltro
    ) throws IllegalArgumentException, RuntimeException {

        if (idsInvernaderoFiltro == null || idsInvernaderoFiltro.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos un idInvernadero.");
        }
        if (magnitudesFiltro == null || magnitudesFiltro.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos una magnitud.");
        }
        if (fechaInicioFiltro == null || fechaFinFiltro == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas.");
        }
        if (fechaInicioFiltro.after(fechaFinFiltro)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        Query queryLecturasCrudas = new Query();
        queryLecturasCrudas.addCriteria(Criteria.where("idInvernadero").in(idsInvernaderoFiltro));
        queryLecturasCrudas.addCriteria(Criteria.where("magnitud").in(magnitudesFiltro));
        queryLecturasCrudas.addCriteria(Criteria.where("fechaHora").gte(fechaInicioFiltro).lte(fechaFinFiltro));

        List<LecturaCruda> lecturasDesdeMongo = mongoTemplate.find(queryLecturasCrudas, LecturaCruda.class, "lecturas");

        if (lecturasDesdeMongo.isEmpty()) {
            System.out.println("InformeService: No se encontraron lecturas crudas para los filtros dados.");
            return new InformeResponseDTO(
                    "Informe Vacío - Sin Lecturas", new Date(),
                    idsInvernaderoFiltro, fechaInicioFiltro, fechaFinFiltro, magnitudesFiltro,
                    new ArrayList<>()
            );
        }
        System.out.println("InformeService: " + lecturasDesdeMongo.size() + " lecturas crudas obtenidas de MongoDB.");

        List<LecturaOriginal> lecturasOriginalesProto = new ArrayList<>();
        for (LecturaCruda lecturaMongo : lecturasDesdeMongo) {
            LecturaOriginal.Builder protoBuilder = LecturaOriginal.newBuilder();

            if (lecturaMongo.get_id() != null) protoBuilder.setIdLectura(lecturaMongo.get_id().toString());
            if (lecturaMongo.getIdSensor() != null) protoBuilder.setIdSensor(lecturaMongo.getIdSensor());
            if (lecturaMongo.getMagnitud() != null) protoBuilder.setMagnitud(lecturaMongo.getMagnitud());
            protoBuilder.setValor(lecturaMongo.getValor());
            if (lecturaMongo.getFechaHora() != null) {
                protoBuilder.setFechaHora(convertDateToTimestamp(lecturaMongo.getFechaHora()));
            }
            if (lecturaMongo.getIdInvernadero() != null) protoBuilder.setIdInvernadero(lecturaMongo.getIdInvernadero());

            lecturasOriginalesProto.add(protoBuilder.build());
        }

        List<LecturaEnriquecida> lecturasEnriquecidasProto = clienteGestionSensores.enriquecerLecturasConDetallesSensor(lecturasOriginalesProto);

        if (lecturasEnriquecidasProto.isEmpty() && !lecturasOriginalesProto.isEmpty()) {
            System.err.println("InformeService: No se pudieron enriquecer las lecturas desde GestionSensores.");
        }

        List<LecturaDTO> lecturasFinalesParaInforme = new ArrayList<>();
        for (LecturaEnriquecida proto : lecturasEnriquecidasProto) {

            LecturaCruda originalCorrespondiente = lecturasDesdeMongo.stream()
                    .filter(lc -> lc.get_id().toString().equals(proto.getIdLectura()))
                    .findFirst().orElse(null);

            LecturaDTO dto = new LecturaDTO();
            dto.set_id(proto.getIdLectura());
            dto.setIdSensor(proto.getIdSensor());
            dto.setMagnitud(proto.getMagnitud());
            dto.setValor(proto.getValor());
            dto.setFechaHora(convertTimestampToDate(proto.getFechaHora()));
            dto.setIdInvernadero(proto.getIdInvernadero());
            dto.setSector(proto.getSector()); // Dato enriquecido
            dto.setFila(proto.getFila());     // Dato enriquecido

            if (originalCorrespondiente != null) {
                dto.setMacAddress(originalCorrespondiente.getMacAddress());
                dto.setMarca(originalCorrespondiente.getMarca());
                dto.setModelo(originalCorrespondiente.getModelo());
                dto.setUnidad(originalCorrespondiente.getUnidad());
                dto.setNombreInvernadero(originalCorrespondiente.getNombreInvernadero());
            }

            lecturasFinalesParaInforme.add(dto);
        }
        System.out.println("InformeService: " + lecturasFinalesParaInforme.size() + " lecturas procesadas y mapeadas a DTO para el informe.");

        String titulo = String.format("Informe de %s para Invernaderos (%s) del %tF al %tF",
                String.join(", ", magnitudesFiltro),
                String.join(", ", idsInvernaderoFiltro),
                fechaInicioFiltro,
                fechaFinFiltro
        );

        return new InformeResponseDTO(
                titulo,
                new Date(),
                idsInvernaderoFiltro,
                fechaInicioFiltro,
                fechaFinFiltro,
                magnitudesFiltro,
                lecturasFinalesParaInforme
        );
    }

    /**
     * Obtiene la lista de nombres de magnitud únicos existentes
     * en la colección principal de lecturas.
     * @return Lista de strings con las magnitudes únicas.
     */
    public List<String> obtenerMagnitudesDisponibles() {
        Query query = new Query();
        return mongoTemplate.findDistinct(query, "magnitud", "lecturas", String.class);
    }

    private Timestamp convertDateToTimestamp(Date date) {
        if (date == null) {
            return Timestamp.newBuilder().build();
        }
        Instant instant = date.toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private Date convertTimestampToDate(Timestamp timestamp) {
        if (timestamp == null || (timestamp.getSeconds() == 0 && timestamp.getNanos() == 0)) {
            return null;
        }
        return Date.from(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()));
    }
}

