package org.itson.Informes.service;

import org.itson.Informes.collections.InformeLectura;
import org.itson.Informes.persistence.IInformeLecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List; // Importar List

@Service
public class InformeService {

    @Autowired
    private IInformeLecturasRepository informeLecturaRepository;

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
}
