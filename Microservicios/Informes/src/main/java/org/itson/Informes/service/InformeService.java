package org.itson.Informes.service;

import org.itson.Informes.collections.InformeLectura;
import org.itson.Informes.persistence.IInformeLecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List; // Importar List

@Service
public class InformeService {

    @Autowired
    private IInformeLecturasRepository informeLecturaRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<InformeLectura> obtenerInformesFiltrados(
            List<String> idsInvernadero,
            Date fechaInicio,
            Date fechaFin,
            List<String> magnitudes
    ) throws IllegalArgumentException {

        if (idsInvernadero == null || idsInvernadero.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos un idInvernadero.");
        }
        if (magnitudes == null || magnitudes.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos una magnitud.");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas.");
        }
        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        List<InformeLectura> lecturasEncontradas = informeLecturaRepository.findInformeLecturasByFiltros(
                idsInvernadero, fechaInicio, fechaFin, magnitudes
        );

        return lecturasEncontradas;
    }

    /**
     * Obtiene la lista de nombres de magnitud únicos existentes
     * en la colección local de lecturas para informes ('lecturas_informes_db.lecturas').
     * @return Lista de strings con las magnitudes únicas (ej: ["Humedad", "Temperatura"]).
     */
    public List<String> obtenerMagnitudesDisponibles() {
        Query query = new Query();
        List<String> magnitudesUnicas = mongoTemplate.findDistinct(query, "magnitud", InformeLectura.class, String.class);
        return magnitudesUnicas;
    }

}
