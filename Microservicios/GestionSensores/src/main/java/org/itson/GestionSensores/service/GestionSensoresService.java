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
     * @param idSensor ID del sensor a buscar.
     * @return El sensor que se haya encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    public SensorDTO obtenerSensorPorId(String idSensor) throws GestionSensoresException {
        Optional<Sensor> sensorEntidad = gestionSensoresRepository.findByIdSensor(idSensor); // Se busca el sensor.
        if (sensorEntidad.isPresent()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            SensorDTO sensorDTO = convertirSensorEntidadDTO(sensorEntidad.get());
            return sensorDTO;
        } else {
            throw new GestionSensoresException("Sensor con ID " + idSensor + " no encontrado.");
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
        if (gestionSensoresRepository.findByMacAddress(sensorDTO.getMacAddress()).isPresent()) {
            throw new GestionSensoresException("Ya hay un sensor con la dirección MAC: " + sensorDTO.getMacAddress() + ".");
        }
        if (gestionSensoresRepository.findByIdSensor(sensorDTO.getIdSensor()).isPresent()) {
            throw new GestionSensoresException("Ya hay un sensor con el ID: " + sensorDTO.getIdSensor() + ".");
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
            sensorEntidad.setTipoSensor(sensorDTO.getTipoSensor());
            sensorEntidad.setMagnitud(sensorDTO.getMagnitud());
            // Si esto tira excepción es porque no existe el invernadero.
            obtenerInvernaderoPorId(new ObjectId(sensorDTO.getIdInvernadero()));
            sensorEntidad.setIdInvernadero(new ObjectId(sensorDTO.getIdInvernadero()));
            sensorEntidad.setSector(sensorDTO.getSector());
            sensorEntidad.setFila(sensorDTO.getFila());
            sensorEntidad.setEstado(sensorDTO.isEstado());
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
     *   \_____\____/|_| \_|   \/   |______|_|  \_\_____/ \____/|_|  \_\______|_____/
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
    public SensorDTO convertirSensorEntidadDTO(Sensor sensorEntidad) throws GestionSensoresException {
        return new SensorDTO(
                sensorEntidad.get_id().toString(),
                sensorEntidad.getIdSensor(),
                sensorEntidad.getMacAddress(),
                sensorEntidad.getMarca(),
                sensorEntidad.getModelo(),
                sensorEntidad.getTipoSensor(),
                sensorEntidad.getMagnitud(),
                sensorEntidad.getIdInvernadero().toString(),
                obtenerInvernaderoPorId(sensorEntidad.getIdInvernadero()).getNombre(),
                sensorEntidad.getSector(),
                sensorEntidad.getFila(),
                sensorEntidad.isEstado()
        );
    }

    /**
     * Método que convierte sensor de tipo DTO a tipo Entidad.
     *
     * @param sensorDTO Sensor DTO a convertir.
     * @return El sensor de tipo Entidad.
     */
    public Sensor convertirSensorDTOEntidad(SensorDTO sensorDTO) throws GestionSensoresException {
        return new Sensor(
                sensorDTO.getIdSensor(),
                sensorDTO.getMacAddress(),
                sensorDTO.getMarca(),
                sensorDTO.getModelo(),
                sensorDTO.getTipoSensor(),
                sensorDTO.getMagnitud(),
                new ObjectId(sensorDTO.getIdInvernadero()),
                sensorDTO.getSector(),
                sensorDTO.getFila()
        );
    }

    /**
     *   _____ _   ___      ________ _____  _   _          _____  ______ _____   ____   _____
     *  |_   _| \ | \ \    / /  ____|  __ \| \ | |   /\   |  __ \|  ____|  __ \ / __ \ / ____|
     *    | | |  \| |\ \  / /| |__  | |__) |  \| |  /  \  | |  | | |__  | |__) | |  | | (___
     *    | | | . ` | \ \/ / |  __| |  _  /| . ` | / /\ \ | |  | |  __| |  _  /| |  | |\___ \
     *   _| |_| |\  |  \  /  | |____| | \ \| |\  |/ ____ \| |__| | |____| | \ \| |__| |____) |
     *  |_____|_| \_|   \/   |______|_|  \_\_| \_/_/    \_\_____/|______|_|  \_\\____/|_____/
     */

    /**
     * Método para obtener un invernadero dado su nombre.
     *
     * @param nombreInvernadero El nombre del invernadero a buscar.
     * @return El invernadero encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    public Invernadero obtenerInvernaderoPorNombre(String nombreInvernadero) throws GestionSensoresException {
        Optional<Invernadero> invernadero = invernaderosRepository.findByNombre(nombreInvernadero);
        if (invernadero.isPresent()) { // Si se obtuvo algo, se regresa.
            return invernadero.get();
        } else {
            throw new GestionSensoresException("No se encontró el invernadero.");
        }
    }

    /**
     * Método para obtener un invernadero dado su ID.
     *
     * @param idInvernadero El ID del invernadero a buscar.
     * @return El invernadero encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    private Invernadero obtenerInvernaderoPorId(ObjectId idInvernadero) throws GestionSensoresException {
        Optional<Invernadero> invernadero = invernaderosRepository.findById(idInvernadero);
        if (invernadero.isPresent()) { // Si se obtuvo algo, se regresa.
            return invernadero.get();
        } else {
            throw new GestionSensoresException("No se encontró el invernadero.");
        }
    }
}
