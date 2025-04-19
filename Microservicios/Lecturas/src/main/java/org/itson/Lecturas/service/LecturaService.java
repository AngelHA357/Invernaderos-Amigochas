package org.itson.Lecturas.service;

import com.itson.grpc.SensorLectura;
import com.itson.grpc.SensorRespuesta;
import io.grpc.StatusRuntimeException;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.itson.Lecturas.collections.Lectura;
import org.itson.Lecturas.excepciones.LecturasException;
import org.itson.Lecturas.persistence.ILecturasRepository;
import org.itson.Lecturas.proto.ClienteGestionSensoresGRPC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LecturaService {
    // Para la inyección de dependencias.
    @Autowired
    private ILecturasRepository lecturasRepository;

    @Autowired
    private ClienteGestionSensoresGRPC clienteGRPC;

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
        SensorRespuesta sensorRespuestaGRPC = clienteGRPC.obtenerSensor(new SensorLectura(
                lecturaColeccion.getIdSensor(),
                lecturaColeccion.getMacAddress(),
                lecturaColeccion.getMarca(),
                lecturaColeccion.getModelo(),
                lecturaColeccion.getTipoLectura(),
                lecturaColeccion.getMagnitud()));

        return new LecturaDTO(
                lecturaColeccion.get_id(),
                lecturaColeccion.getIdSensor(),
                lecturaColeccion.getMacAddress(),
                lecturaColeccion.getMarca(),
                lecturaColeccion.getModelo(),
                lecturaColeccion.getTipoLectura(),
                lecturaColeccion.getMagnitud(),
                lecturaColeccion.getValor(),
                lecturaColeccion.getFechaHora(),
                sensorRespuestaGRPC.getNombreInvernadero(),
                sensorRespuestaGRPC.getSector(),
                sensorRespuestaGRPC.getFila()
        );
    }

    /**
     * Método que convierte una lectura de tipo DTO a tipo Colección.
     *
     * @param lecturaDTO Lectura DTO a convertir.
     * @return La lectura de tipo Colección.
     */
    private Lectura conversorLecturaDTOColeccion(LecturaDTO lecturaDTO) throws LecturasException {
        SensorRespuesta sensorRespuestaGRPC = clienteGRPC.obtenerSensor(new SensorLectura(
                lecturaDTO.getIdSensor(),
                lecturaDTO.getMacAddress(),
                lecturaDTO.getMarca(),
                lecturaDTO.getModelo(),
                lecturaDTO.getTipoLectura(),
                lecturaDTO.getMagnitud()));
        return new Lectura(
                lecturaDTO.getIdSensor(),
                lecturaDTO.getMacAddress(),
                lecturaDTO.getMarca(),
                lecturaDTO.getModelo(),
                lecturaDTO.getTipoLectura(),
                lecturaDTO.getMagnitud(),
                lecturaDTO.getValor(),
                lecturaDTO.getFechaHora(),
                sensorRespuestaGRPC.getNombreInvernadero(),
                sensorRespuestaGRPC.getSector(),
                sensorRespuestaGRPC.getFila()
        );
    }
}