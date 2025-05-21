package org.itson.ExposicionDatos.service;

import org.itson.ExposicionDatos.collections.Lectura;
import org.itson.ExposicionDatos.dtos.LecturaDTO;
import org.itson.ExposicionDatos.exceptions.ExposicionDatosException;
import org.itson.ExposicionDatos.persistence.ILecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExposicionDatosService {
    // Para la inyección de dependencias.
    @Autowired
    private ILecturasRepository lecturasRepository;

    /**
     * Método que obtiene todas las lecturas.
     *
     * @return Una lista con todas las lecturas encontradas.
     * @throws ExposicionDatosException en caso de que no se haya encontrado ninguna lectura.
     */
    public List<LecturaDTO> obtenerTodasLecturas() throws ExposicionDatosException {
        List<Lectura> lecturasColeccion = lecturasRepository.findAll(); // Se obtienen los lecturas.
        if (!lecturasColeccion.isEmpty()) {
            List<LecturaDTO> lecturasDTO = conversorLecturasColeccionDTO(lecturasColeccion); // Se convierten a DTO.
            return lecturasDTO; // Se devuelven.
        } else {
            throw new ExposicionDatosException("No se encontró ninguna lectura.");
        }
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
    private List<LecturaDTO> conversorLecturasColeccionDTO(List<Lectura> lecturasColeccion) throws ExposicionDatosException {
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
     * @throws ExposicionDatosException en caso de que no se haya encontrado la lectura.
     */
    private LecturaDTO conversorLecturaColeccionDTO(Lectura lecturaColeccion) throws ExposicionDatosException {

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

}