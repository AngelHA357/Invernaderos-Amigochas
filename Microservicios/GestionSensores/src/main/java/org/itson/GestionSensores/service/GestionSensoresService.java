package org.itson.GestionSensores.service;

import com.mongodb.MongoWriteException;
import org.bson.types.ObjectId;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.collections.Sensor;
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
            List<SensorDTO> sensoresDTO = convertirSensoresEntidadDTO(sensoresEntidad); // Se convierten a DTO.
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
    public SensorDTO obtenerSensorPorId(String id) throws GestionSensoresException {
        Optional<Sensor> sensorEntidad = gestionSensoresRepository.findById(new ObjectId(id)); // Se busca el sensor.
        if (sensorEntidad.isPresent()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            SensorDTO sensorDTO = convertirSensorEntidadDTO(sensorEntidad.get());
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
        if (gestionSensoresRepository.findByMacAddress(sensorDTO.getMacAddress()) == null) {
            throw new GestionSensoresException("Ya hay un sensor con la dirección MAC: " + sensorDTO.getMacAddress() + ".");
        }
        Sensor sensorEntidad = convertirSensorDTOEntidad(sensorDTO);
        Sensor resultado = gestionSensoresRepository.save(sensorEntidad);
        return convertirSensorEntidadDTO(resultado);
    }

    /**
     * Método para editar los datos de un sensor.
     *
     * @param sensorDTO El sensor a editar.
     * @return El sensor que se editó.
     * @throws GestionSensoresException En caso de que ocurra un error durante la edición.
     */
    public SensorDTO editarSensor(SensorDTO sensorDTO) throws GestionSensoresException {
        Sensor resultado = null;
        Optional<Sensor> sensorObtenido = gestionSensoresRepository.findById(new ObjectId(sensorDTO.get_id()));

        if (sensorObtenido.isPresent()) {
            // Si se encontró algo, se convierte a entidad.
            Sensor sensorEntidad = sensorObtenido.get();
            sensorEntidad.setMacAddress(sensorDTO.getMacAddress());
            sensorEntidad.setMarca(sensorDTO.getMarca());
            sensorEntidad.setModelo(sensorDTO.getModelo());
            // Si esto tira excepción es porque no existe el invernadero.
            obtenerInvernadero(new ObjectId(sensorDTO.getIdInvernadero()));
            sensorEntidad.setIdInvernadero(new ObjectId(sensorDTO.getIdInvernadero()));
            sensorEntidad.setSector(sensorDTO.getSector());
            sensorEntidad.setFila(sensorDTO.getFila());
            // Si llegamos a esta parte es porque sí existe el sensor.
            try {
                resultado = gestionSensoresRepository.save(sensorEntidad);
            } catch (MongoWriteException mwe) {
                throw new GestionSensoresException("Ya hay un sensor con la dirección MAC: " + sensorDTO.getMacAddress() + ".");
            }
            // Lo enviamos convertido en DTO.
            return convertirSensorEntidadDTO(resultado);
        } else {
            throw new GestionSensoresException("Sensor con ID " + sensorDTO.get_id() + " no encontrado.");
        }
    }

    /**
     * Método para eliminar un sensor.
     *
     * @param id ID del sensor a eliminar.
     * @throws GestionSensoresException En caso de que ocurra un error durante la eliminación.
     */
    public void eliminarSensor(String id) throws GestionSensoresException {
        Optional<Sensor> sensorObtenido = gestionSensoresRepository.findById(new ObjectId(id));
        System.out.println(sensorObtenido);
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
    private List<SensorDTO> convertirSensoresEntidadDTO(List<Sensor> sensoresEntidad) throws GestionSensoresException {
        List<SensorDTO> sensoresDTO = new ArrayList<>();
        for (Sensor sensorEntidad : sensoresEntidad) {
            sensoresDTO.add(convertirSensorEntidadDTO(sensorEntidad));
        }
        return sensoresDTO;
    }

    /**
     * Método que convierte sensor de tipo Entidad a tipo DTO.
     *
     * @param sensorEntidad Sensor Entidad a convertir.
     * @return El sensor de tipo DTO.
     */
    private SensorDTO convertirSensorEntidadDTO(Sensor sensorEntidad) throws GestionSensoresException {
        return new SensorDTO(
                sensorEntidad.get_id().toString(),
                sensorEntidad.getMacAddress(),
                sensorEntidad.getMarca(),
                sensorEntidad.getModelo(),
                sensorEntidad.getIdInvernadero().toString(),
                obtenerInvernadero(sensorEntidad.getIdInvernadero()).getNombre(),
                sensorEntidad.getSector(),
                sensorEntidad.getFila()
        );
    }

    /**
     * Método que convierte sensor de tipo DTO a tipo Entidad.
     *
     * @param sensorDTO Sensor DTO a convertir.
     * @return El sensor de tipo Entidad.
     */
    private Sensor convertirSensorDTOEntidad(SensorDTO sensorDTO) throws GestionSensoresException {
        return new Sensor(
                sensorDTO.getMacAddress(),
                sensorDTO.getMarca(),
                sensorDTO.getModelo(),
                new ObjectId(sensorDTO.getIdInvernadero()),
                sensorDTO.getSector(),
                sensorDTO.getFila()
        );
    }

    /**
     * Método para obtener un invernadero dado su ID.
     *
     * @param idInvernadero El ID del invernadero a buscar.
     * @return El invernadero encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    private Invernadero obtenerInvernadero(ObjectId idInvernadero) throws GestionSensoresException {
        Optional<Invernadero> invernadero = invernaderosRepository.findById(idInvernadero);
        if (invernadero.isPresent()) { // Si se obtuvo algo, se regresa.
            return invernadero.get();
        } else {
            throw new GestionSensoresException("No se encontró el invernadero.");
        }
    }
}
