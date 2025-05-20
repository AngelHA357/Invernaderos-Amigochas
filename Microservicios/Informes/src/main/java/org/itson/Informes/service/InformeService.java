package org.itson.Informes.service;

import com.google.protobuf.Timestamp;
import org.bson.types.ObjectId;
import org.itson.Informes.dtos.DatosFaltantesDTO;
import org.itson.Informes.dtos.InformeResponseDTO;
import org.itson.Informes.dtos.LecturaDTO;
import org.itson.Informes.collections.LecturaCruda;
import org.itson.Informes.proto.ClienteGestionSensoresGrpc;
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

        List<LecturaCruda> lecturasObtenidas = mongoTemplate.find(queryLecturasCrudas, LecturaCruda.class, "lecturas");

        if (lecturasObtenidas.isEmpty()) {
            System.out.println("InformeService: No se encontraron lecturas crudas para los filtros dados.");
            return new InformeResponseDTO(
                    "Informe Vacío - Sin Lecturas", new Date(),
                    idsInvernaderoFiltro, fechaInicioFiltro, fechaFinFiltro, magnitudesFiltro,
                    new ArrayList<>()
            );
        }
        System.out.println("InformeService: " + lecturasObtenidas.size() + " lecturas crudas obtenidas de MongoDB.");

        List<String> idSensores = new ArrayList<>();
        for (LecturaCruda lectura : lecturasObtenidas) {
            idSensores.add(lectura.getIdSensor());
        }

        List<DatosFaltantesDTO> listaDatosFaltantes = clienteGestionSensores.obtenerDatosFaltantes(idSensores);

        List<LecturaDTO> lecturasEnriquecidas = enriquecerLecturas(listaDatosFaltantes, lecturasObtenidas);

        System.out.println("InformeService: " + lecturasEnriquecidas.size() + " lecturas procesadas y mapeadas a DTO para el informe.");

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
                lecturasEnriquecidas
        );
    }

    private List<LecturaDTO> enriquecerLecturas(List<DatosFaltantesDTO> listaDatosFaltantes, List<LecturaCruda> lecturasDesdeMongo) {
        List<LecturaDTO> lecturasEnriquecidas = new ArrayList<>();
        for (LecturaCruda lectura : lecturasDesdeMongo) {
            for (DatosFaltantesDTO datosFaltantes : listaDatosFaltantes) {
                if (lectura.getIdSensor().equals(datosFaltantes.getIdSensor())) {
                    lectura.setSector(datosFaltantes.getSector());
                    lectura.setFila(datosFaltantes.getFila());
                    lecturasEnriquecidas.add(new LecturaDTO(
                            lectura.get_id().toString(),
                            lectura.getIdSensor(),
                            lectura.getMacAddress(),
                            lectura.getMarca(),
                            lectura.getModelo(),
                            lectura.getMagnitud(),
                            lectura.getUnidad(),
                            lectura.getValor(),
                            lectura.getFechaHora(),
                            lectura.getIdInvernadero(),
                            lectura.getNombreInvernadero(),
                            lectura.getSector(),
                            lectura.getFila()
                    ));
                    break;
                }
            }
        }
        return lecturasEnriquecidas;
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
}

