package org.itson.Lecturas.service;

import io.grpc.StatusRuntimeException;
import org.itson.Lecturas.collections.Lectura;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.itson.Lecturas.excepciones.LecturasException;
import org.itson.Lecturas.persistence.ILecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LecturaService {
    // Para la inyección de dependencias.
    @Autowired
    private ILecturasRepository lecturasRepository;

    /**
     * Método que obtiene todas las lecturas.
     *
     * @return Una lista con todas las lecturas encontradas.
     * @throws LecturasException en caso de que no se haya encontrado ninguna lectura.
     */
    public List<LecturaDTO> obtenerTodasLecturas() throws LecturasException {
        List<Lectura> lecturasColeccion = lecturasRepository.findAll(); // Se obtienen los lecturas.
        if (!lecturasColeccion.isEmpty()) {
            List<LecturaDTO> lecturasDTO = conversorLecturasColeccionDTO(lecturasColeccion); // Se convierten a DTO.
            return lecturasDTO; // Se devuelven.
        } else {
            throw new LecturasException("No se encontró ninguna lectura.");
        }
    }

    /**
     * Método para dar de alta una lectura.
     *
     * @param lecturaDTO La lectura a registrar.
     * @return La lectura que se registró.
     * @throws LecturasException En caso de que ocurra un error durante el registro.
     */
    public LecturaDTO registrarLectura(LecturaDTO lecturaDTO) throws LecturasException {
        Lectura lecturaColeccion = conversorLecturaDTOColeccion(lecturaDTO);
        Lectura resultado = lecturasRepository.save(lecturaColeccion);
        return conversorLecturaColeccionDTO(resultado);
    }

    /**
     * Obtiene una lista de lecturas filtradas según los parámetros
     *
     * @param idInvernadero El ID del invernadero a filtrar.
     * @param fechaInicio La fecha de inicio del rango.
     * @param fechaFin La fecha de fin del rango.
     * @param magnitud El tipo de magnitud a filtrar.
     * @return Una lista de LecturaDTO que coinciden con los filtros. Puede estar vacía.
     * @throws LecturasException Si ocurre un error, como fechas inválidas.
     */
    public List<LecturaDTO> obtenerLecturasFiltradas(String idInvernadero, Date fechaInicio, Date fechaFin, String magnitud) throws LecturasException {
        if (fechaInicio.after(fechaFin)) {
            throw new LecturasException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        List<Lectura> lecturasEncontradas = lecturasRepository.findLecturasByFiltros(idInvernadero, fechaInicio, fechaFin, magnitud);
        List<LecturaDTO> lecturasDTO = conversorLecturasColeccionDTO(lecturasEncontradas);
        return lecturasDTO;
    }

    /**
     *    _____ ____  _   ___      ________ _____   _____  ____  _____  ______  _____
     *   / ____/ __ \| \ | \ \    / /  ____|  __ \ / ____|/ __ \|  __ \|  ____|/ ____|
     *  | |   | |  | |  \| |\ \  / /| |__  | |__) | (___ | |  | | |__) | |__  | (___
     *  | |   | |  | | . ` | \ \/ / |  __| |  _  / \___ \| |  | |  _  /|  __|  \___ \
     *  | |___| |__| | |\  |  \  /  | |____| | \ \ ____) | |__| | | \ \| |____ ____) |
     *  \______\____/|_| \_|   \/   |______|_|  \_\_____/ \____/|_|  \_\______|_____/
     */

    /**
     * Método que convierte una lista de lecturas de tipo Colección a tipo DTO.
     *
     * @param lecturasColeccion Lista lecturas a convertir.
     * @return La lista de lecturas de tipo DTO.
     */
    private List<LecturaDTO> conversorLecturasColeccionDTO(List<Lectura> lecturasColeccion) throws LecturasException {
        List<LecturaDTO> lecturasDTO = new ArrayList<>();
        for (Lectura lecturaColeccion : lecturasColeccion) {
            lecturasDTO.add(conversorLecturaColeccionDTO(lecturaColeccion));
        }
        return lecturasDTO;
    }

    /**
     * Método que convierte una lectura de tipo Colección a tipo DTO.
     *
     * @param lecturaColeccion Lectura a convertir.
     * @return La lectura de tipo DTO.
     */
    private LecturaDTO conversorLecturaColeccionDTO(Lectura lecturaColeccion) throws LecturasException {

        return new LecturaDTO(
                lecturaColeccion.get_id() != null ? lecturaColeccion.get_id().toHexString() : null,
                lecturaColeccion.getIdSensor(),
                lecturaColeccion.getMacAddress(),
                lecturaColeccion.getMarca(),
                lecturaColeccion.getModelo(),
                lecturaColeccion.getMagnitud(),
                lecturaColeccion.getUnidad(),
                lecturaColeccion.getValor(),
                lecturaColeccion.getFechaHora(),
                lecturaColeccion.getIdInvernadero(),
                lecturaColeccion.getNombreInvernadero(),
                lecturaColeccion.getSector(),
                lecturaColeccion.getFila()
        );
    }

    /**
     * Método que convierte una lectura de tipo DTO a tipo Colección.
     *
     * @param lecturaDTO Lectura DTO a convertir.
     * @return La lectura de tipo Colección.
     */
    private Lectura conversorLecturaDTOColeccion(LecturaDTO lecturaDTO) throws LecturasException {

        return new Lectura(
                lecturaDTO.getIdSensor(),
                lecturaDTO.getMacAddress(),
                lecturaDTO.getMarca(),
                lecturaDTO.getModelo(),
                lecturaDTO.getMagnitud(),
                lecturaDTO.getUnidad(),
                lecturaDTO.getValor(),
                lecturaDTO.getFechaHora(),
                lecturaDTO.getIdInvernadero(),
                lecturaDTO.getNombreInvernadero(),
                lecturaDTO.getSector(),
                lecturaDTO.getFila()
        );
    }
}