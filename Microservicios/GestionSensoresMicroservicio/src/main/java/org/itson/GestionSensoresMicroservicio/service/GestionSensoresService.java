package org.itson.GestionSensoresMicroservicio.service;

import org.itson.GestionSensoresMicroservicio.persistence.IGestionSensoresRepository;
import org.itson.GestionSensoresMicroservicio.entities.Sensor;
import org.itson.GestionSensoresMicroservicio.dtos.SensorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de servicio para la gestión de sensores. Contiene las reglas de negocio.
 */
@Service
public class GestionSensoresService {
    // Para la inyección de dependencias.
    @Autowired
    private IGestionSensoresRepository gestionSensoresRepository;

    /**
     * Método que obtiene los sensores de tipo Modelo y los convierte a DTO.
     * @return
     */
    public List<SensorDTO> obtenerTodosSensores() {
        List<Sensor> sensoresModelo = gestionSensoresRepository.findAll(); // Se obtienen los sensores.
        List<SensorDTO> sensoresDTO = conversorSensorModeloDTO(sensoresModelo); // Se convierten a DTO.
        return sensoresDTO; // Se devuelven.
    }

    /**
     * Método que convierte una lista de sensores de tipo Modelo a tipo DTO.
     *
     * @param sensoresModelo Lista Modelo a convertir.
     * @return La lista de sensores de tipo DTO.
     */
    private List<SensorDTO> conversorSensorModeloDTO(List<Sensor> sensoresModelo) {
        List<SensorDTO> sensoresDTO = new ArrayList<>();
        for (Sensor sensorModelo : sensoresModelo) {
            sensoresDTO.add(new SensorDTO(
                    sensorModelo.getId(),
                    sensorModelo.getMacAddress(),
                    sensorModelo.getMarca(),
                    sensorModelo.getModelo(),
                    sensorModelo.getInvernadero().getNombre()
            ));
        }
        return sensoresDTO;
    }
}
