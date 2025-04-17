package org.itson.GestionSensores.service;

import org.itson.GestionSensores.entities.Invernadero;
import org.itson.GestionSensores.entities.Sensor;
import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.persistence.IInvernaderosRepository;
import org.itson.GestionSensores.persistence.IGestionSensoresRepository;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase de servicio para la gestión de sensores. Contiene las reglas de negocio.
 */
@Service
public class GestionSensoresService {
    // Para la inyección de dependencias.
    @Autowired
    private IGestionSensoresRepository gestionSensoresRepository;
    @Autowired
    private IInvernaderosRepository invernaderosRepository;

    /**
     * Método que obtiene todos los sensores.
     *
     * @return Una lista con todos los sensores encontrados.
     * @throws GestionSensoresException en caso de que no se haya encontrado ningún sensor.
     */
    public List<SensorDTO> obtenerTodosSensores() throws GestionSensoresException {
        List<Sensor> sensoresEntidad = gestionSensoresRepository.findAll(); // Se obtienen los sensores.
        if (!sensoresEntidad.isEmpty()) {
            List<SensorDTO> sensoresDTO = conversorSensoresEntidadDTO(sensoresEntidad); // Se convierten a DTO.
            return sensoresDTO; // Se devuelven.
        } else {
            throw new GestionSensoresException("No se encontró ningún sensor.");
        }
    }

    /**
     * Método que obtiene un sensor dado su ID.
     *
     * @param id ID del sensor a buscar.
     * @return El sensor que se haya encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    public SensorDTO obtenerSensorPorId(Long id) throws GestionSensoresException {
        Optional<Sensor> sensorEntidad = gestionSensoresRepository.findById(id); // Se busca el sensor.
        if (sensorEntidad.isPresent()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            SensorDTO sensorDTO = conversorSensorEntidadDTO(sensorEntidad.get());
            return sensorDTO;
        } else {
            throw new GestionSensoresException("Sensor con ID " + id + " no encontrado.");
        }

    }

    /**
     * Método para dar de alta un sensor.
     *
     * @param sensorDTO El sensor a registrar.
     * @return El sensor que se registró.
     * @throws GestionSensoresException En caso de que ocurra un error durante el registro.
     */
    public SensorDTO registrarSensor(SensorDTO sensorDTO) throws GestionSensoresException {
        Sensor sensorEntidad = conversorSensorDTOEntidad(sensorDTO);
        Sensor resultado = gestionSensoresRepository.save(sensorEntidad);
        return conversorSensorEntidadDTO(resultado);
    }

    /**
     * Método para editar los datos de un sensor.
     *
     * @param sensorDTO El sensor a editar.
     * @return El sensor que se editó.
     * @throws GestionSensoresException En caso de que ocurra un error durante la edición.
     */
    public SensorDTO editarSensor(SensorDTO sensorDTO) throws GestionSensoresException {
        Optional<Sensor> sensorObtenido = gestionSensoresRepository.findById(sensorDTO.getId());
        if (sensorObtenido.isPresent()) {
            Sensor sensorEntidad = sensorObtenido.get(); // Si se encontró algo, se convierte a entidad.
            sensorEntidad.setMarca(sensorDTO.getMarca());
            sensorEntidad.setModelo(sensorDTO.getModelo());
            // Aquí obtenemos la entidad del invernadero en caso de que se haya editado.
            if (!sensorEntidad.getInvernadero().getNombre().equalsIgnoreCase(sensorDTO.getInvernadero())) {
                sensorEntidad.setInvernadero(obtenerInvernadero(sensorDTO.getInvernadero()));
            }
            // Si llegamos a esta parte es porque sí existe el sensor.
            Sensor resultado = gestionSensoresRepository.save(sensorEntidad);
            return conversorSensorEntidadDTO(resultado);
        } else {
            throw new GestionSensoresException("Sensor con ID " + sensorDTO.getId() + " no encontrado.");
        }
    }

    /**
     * Método para eliminar un sensor.
     *
     * @param id ID del sensor a eliminar.
     * @throws GestionSensoresException En caso de que ocurra un error durante la eliminación.
     */
    public void eliminarSensor(Long id) throws GestionSensoresException {
        Optional<Sensor> sensorObtenido = gestionSensoresRepository.findById(id);
        if (sensorObtenido.isPresent()) {
            gestionSensoresRepository.delete(sensorObtenido.get()); // Si llegamos a esta parte es porque sí existe el sensor.
        } else {
            throw new GestionSensoresException("Sensor con ID " + id + " no encontrado.");
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
     * Método que convierte una lista de sensores de tipo Entidad a tipo DTO.
     *
     * @param sensoresEntidad Lista Entidad a convertir.
     * @return La lista de sensores de tipo DTO.
     */
    private List<SensorDTO> conversorSensoresEntidadDTO(List<Sensor> sensoresEntidad) {
        List<SensorDTO> sensoresDTO = new ArrayList<>();
        for (Sensor sensorEntidad : sensoresEntidad) {
            sensoresDTO.add(conversorSensorEntidadDTO(sensorEntidad));
        }
        return sensoresDTO;
    }

    /**
     * Método que convierte sensor de tipo Entidad a tipo DTO.
     *
     * @param sensorEntidad Sensor Entidad a convertir.
     * @return El sensor de tipo DTO.
     */
    private SensorDTO conversorSensorEntidadDTO(Sensor sensorEntidad) {
        return new SensorDTO(
                sensorEntidad.getId(),
                sensorEntidad.getMacAddress(),
                sensorEntidad.getMarca(),
                sensorEntidad.getModelo(),
                sensorEntidad.getInvernadero().getNombre()
        );
    }

    /**
     * Método que convierte sensor de tipo DTO a tipo Entidad.
     *
     * @param sensorDTO Sensor DTO a convertir.
     * @return El sensor de tipo Entidad.
     */
    private Sensor conversorSensorDTOEntidad(SensorDTO sensorDTO) throws GestionSensoresException {
        return new Sensor(
                sensorDTO.getMacAddress(),
                sensorDTO.getMarca(),
                sensorDTO.getModelo(),
                obtenerInvernadero(sensorDTO.getInvernadero())
        );
    }

    /**
     * Método para obtener un invernadero dado su nombre.
     *
     * @param nombreInvernadero El nombre del invernadero a buscar.
     * @return El invernadero encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    private Invernadero obtenerInvernadero(String nombreInvernadero) throws GestionSensoresException {
        Optional<Invernadero> invernadero = invernaderosRepository.findByNombreIgnoreCase(nombreInvernadero);
        if (invernadero.isPresent()) { // Si se obtuvo algo, se regresa.
            return invernadero.get();
        } else {
            throw new GestionSensoresException("No se encontró el invernadero.");
        }
    }
}
