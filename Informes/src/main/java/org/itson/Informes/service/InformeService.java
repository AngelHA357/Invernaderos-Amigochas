package org.itson.Informes.service;

import com.google.protobuf.Timestamp;
import org.bson.types.ObjectId;
import org.itson.Informes.dtos.DatosFaltantesDTO;
import org.itson.Informes.dtos.InformeResponseDTO;
import org.itson.Informes.dtos.InvernaderoBasicoDTO;
import org.itson.Informes.dtos.LecturaDTO;
import org.itson.Informes.collections.LecturaCruda;
import org.itson.Informes.proto.ClienteGestionSensoresGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

        System.out.println("----------------------------------------------------");
        System.out.println("[InformeService DEBUG] Iniciando generarInformeParaFrontend");
        System.out.println("[InformeService DEBUG] idsInvernaderoFiltro: " + idsInvernaderoFiltro);
        System.out.println("[InformeService DEBUG] magnitudesFiltro: " + magnitudesFiltro);
        System.out.println("[InformeService DEBUG] fechaInicioFiltro (Date object): " + fechaInicioFiltro);
        System.out.println("[InformeService DEBUG] fechaFinFiltro (Date object): " + fechaFinFiltro);
        System.out.println("[InformeService DEBUG] Query MongoDB (antes de ejecutar): " + queryLecturasCrudas.toString());

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

    /**
     * Obtiene la lista de invernaderos únicos (ID y Nombre) presentes
     * en la colección principal de lecturas.
     * @return Lista de InvernaderoBasicoDTO.
     */
    public List<InvernaderoBasicoDTO> obtenerInvernaderosDesdeLecturas() {
        GroupOperation groupByInvernadero = group("idInvernadero")
                .first("nombreInvernadero").as("nombre");

        ProjectionOperation projectToDTOFormat = project()
                .andExpression("_id").as("id") // _id aquí es el idInvernadero del group
                .and("nombre").as("nombre");
        // No es necesario .andExclude("_id") si no hay un campo "_id" explícito en InvernaderoBasicoDTO
        // o si se quiere el _id original del grupo (que es el idInvernadero)

        Aggregation aggregation = Aggregation.newAggregation(groupByInvernadero, projectToDTOFormat);

        List<InvernaderoBasicoDTO> resultados = mongoTemplate.aggregate(
                aggregation, "lecturas", InvernaderoBasicoDTO.class
        ).getMappedResults();

        System.out.println("[InformeService INFO] Invernaderos únicos obtenidos de agregación: " + resultados.size());
        resultados.forEach(dto -> System.out.println("[InformeService DEBUG] Agregado: ID=" + dto.getId() + ", Nombre=" + dto.getNombre()));


        // Filtro más robusto para asegurar que tanto id como nombre sean válidos y no solo espacios en blanco
        List<InvernaderoBasicoDTO> invernaderosFiltrados = resultados.stream()
                .filter(dto -> {
                    boolean idValido = dto.getId() != null && !dto.getId().trim().isEmpty();
                    boolean nombreValido = dto.getNombre() != null && !dto.getNombre().trim().isEmpty();
                    if (!idValido) {
                        System.out.println("[InformeService WARN] Filtrando InvernaderoBasicoDTO con ID inválido: " + dto);
                    }
                    if (!nombreValido) {
                        System.out.println("[InformeService WARN] Filtrando InvernaderoBasicoDTO con Nombre inválido: " + dto);
                    }
                    return idValido && nombreValido;
                })
                .collect(Collectors.toList());

        System.out.println("[InformeService INFO] Invernaderos únicos filtrados y válidos a enviar: " + invernaderosFiltrados.size());
        invernaderosFiltrados.forEach(dto -> System.out.println("[InformeService DEBUG] Enviando Invernadero Filtrado: ID=" + dto.getId() + ", Nombre=" + dto.getNombre()));

        return invernaderosFiltrados;
    }

    private List<LecturaDTO> enriquecerLecturas(List<DatosFaltantesDTO> listaDatosFaltantes, List<LecturaCruda> lecturasDesdeMongo) {
        List<LecturaDTO> lecturasEnriquecidas = new ArrayList<>();
        System.out.println("[InformeService DEBUG] enriquecerLecturas - DatosFaltantesDTO recibidos de gRPC: " + listaDatosFaltantes.size() + " elementos.");
        // Opcional: Imprimir todos los DatosFaltantesDTO
        // listaDatosFaltantes.forEach(df -> System.out.println("[InformeService DEBUG] gRPC DTO: " + df));

        for (LecturaCruda lectura : lecturasDesdeMongo) {
            boolean enriquecido = false; // Bandera para saber si se enriqueció
            for (DatosFaltantesDTO datosFaltantes : listaDatosFaltantes) {
                if (lectura.getIdSensor() != null && lectura.getIdSensor().equals(datosFaltantes.getIdSensor())) {
                    System.out.println("[InformeService DEBUG] Enriqueciendo lectura IDSensor: " + lectura.getIdSensor() +
                            " con DatosFaltantes IDSensor: " + datosFaltantes.getIdSensor() +
                            ", Sector: " + datosFaltantes.getSector() + ", Fila: " + datosFaltantes.getFila());

                    // Guardar los valores originales de LecturaCruda antes de sobreescribir por si acaso
                    // String sectorOriginal = lectura.getSector();
                    // String filaOriginal = lectura.getFila();

                    lectura.setSector(datosFaltantes.getSector()); // Actualiza el objeto LecturaCruda
                    lectura.setFila(datosFaltantes.getFila());     // Actualiza el objeto LecturaCruda

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
                            lectura.getSector(), // Usa el sector actualizado
                            lectura.getFila()    // Usa la fila actualizada
                    ));
                    enriquecido = true;
                    break;
                }
            }
            if (!enriquecido) {
                // Esto pasará si el idSensor de la LecturaCruda no se encontró en listaDatosFaltantes
                System.out.println("[InformeService WARN] LecturaCruda con IDSensor: " + lectura.getIdSensor() + " no encontró datos de enriquecimiento vía gRPC. Se usarán sector/fila de LecturaCruda si existen.");
                lecturasEnriquecidas.add(new LecturaDTO( // Se añaden los datos originales de LecturaCruda
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
                        lectura.getSector(), // Sector original de LecturaCruda
                        lectura.getFila()    // Fila original de LecturaCruda
                ));
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

