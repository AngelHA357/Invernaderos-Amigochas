package org.itson.Lecturas.service;

import org.itson.Lecturas.dtos.LecturaDTO;
import org.itson.Lecturas.entities.Lectura;
import org.itson.Lecturas.excepciones.LecturasException;
import org.itson.Lecturas.persistence.ILecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<Lectura> lecturasEntidad = lecturasRepository.findAll(); // Se obtienen los lecturas.
        if (!lecturasEntidad.isEmpty()) {
            List<LecturaDTO> lecturasDTO = conversorLecturasEntidadDTO(lecturasEntidad); // Se convierten a DTO.
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
        Lectura lecturaEntidad = conversorLecturaDTOEntidad(lecturaDTO);
        Lectura resultado = lecturasRepository.save(lecturaEntidad);
        return conversorLecturaEntidadDTO(resultado);
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
     * Método que convierte una lista de lecturas de tipo Entidad a tipo DTO.
     *
     * @param lecturasEntidad Lista Entidad a convertir.
     * @return La lista de lecturas de tipo DTO.
     */
    private List<LecturaDTO> conversorLecturasEntidadDTO(List<Lectura> lecturasEntidad) {
        List<LecturaDTO> lecturasDTO = new ArrayList<>();
        for (Lectura lecturaEntidad : lecturasEntidad) {
            lecturasDTO.add(conversorLecturaEntidadDTO(lecturaEntidad));
        }
        return lecturasDTO;
    }

    /**
     * Método que convierte una lectura de tipo Entidad a tipo DTO.
     *
     * @param lecturaEntidad Lectura Entidad a convertir.
     * @return La lectura de tipo DTO.
     */
    private LecturaDTO conversorLecturaEntidadDTO(Lectura lecturaEntidad) {
        return new LecturaDTO(
                lecturaEntidad.getId(),
                lecturaEntidad.getIdSensor(),
                lecturaEntidad.getTipoSensor(),
                lecturaEntidad.getValor(),
                lecturaEntidad.getTimestamp()
        );
    }

    /**
     * Método que convierte una lectura de tipo DTO a tipo Entidad.
     *
     * @param lecturaDTO Lectura DTO a convertir.
     * @return La lectura de tipo Entidad.
     */
    private Lectura conversorLecturaDTOEntidad(LecturaDTO lecturaDTO) throws LecturasException {
        return new Lectura(
                lecturaDTO.getIdLectura(),
                lecturaDTO.getIdSensor(),
                lecturaDTO.getTipoSensor(),
                lecturaDTO.getValor(),
                lecturaDTO.getTimestamp()
        );
    }
}