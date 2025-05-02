package org.itson.Informes.service;

import org.itson.Informes.collections.InformeLectura;
import org.itson.Informes.persistence.IInformeLecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InformeService {

    @Autowired
    private IInformeLecturasRepository informeLecturaRepository;

    /**
     * Busca en la base de datos local de informes las lecturas que coincidan
     * con los filtros proporcionados. Es llamado por el InformesController.
     *
     * @param idInvernadero El ID del invernadero.
     * @param fechaInicio   La fecha de inicio del rango.
     * @param fechaFin      La fecha de fin del rango.
     * @param magnitud      El tipo de magnitud.
     * @return Una lista de InformeLectura que coinciden con los filtros.
     * @throws IllegalArgumentException Si las fechas son inválidas.
     */
    public List<InformeLectura> obtenerInformesFiltrados(String idInvernadero, Date fechaInicio, Date fechaFin, String magnitud)
            throws IllegalArgumentException {

        if (idInvernadero == null || idInvernadero.trim().isEmpty()) {
            throw new IllegalArgumentException("El idInvernadero no puede estar vacío.");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas.");
        }
        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        if (magnitud == null || magnitud.trim().isEmpty()) {
            throw new IllegalArgumentException("La magnitud no puede estar vacía.");
        }

        List<InformeLectura> lecturasEncontradas = informeLecturaRepository.findInformeLecturasByFiltros(
                idInvernadero, fechaInicio, fechaFin, magnitud
        );

        return lecturasEncontradas;
    }

}
