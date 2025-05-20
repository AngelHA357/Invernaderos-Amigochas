package org.itson.GestionSensores.service;

import com.mongodb.MongoWriteException;
import org.bson.types.ObjectId;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.collections.Sensor;
import org.itson.GestionSensores.dtos.DatosFaltantesDTO;
import org.itson.GestionSensores.dtos.InvernaderoDTO;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.persistence.IGestionSensoresRepository;
import org.itson.GestionSensores.persistence.IInvernaderosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.itson.GestionSensores.dtos.InvernaderoBasicoDTO;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Sensor> sensoresEntidad = gestionSensoresRepository.findAll();
        if (!sensoresEntidad.isEmpty()) {
            return convertirSensoresEntidadDTO(sensoresEntidad);
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
        Optional<Sensor> sensorEntidad = gestionSensoresRepository.findByIdSensor(idSensor);
        if (sensorEntidad.isPresent()) {
            return convertirSensorEntidadDTO(sensorEntidad.get());
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
        // Validar unicidad de macAddress e idSensor
        if (gestionSensoresRepository.findByMacAddress(sensorDTO.getMacAddress()).isPresent()) {
            throw new GestionSensoresException("Ya hay un sensor con la dirección MAC: " + sensorDTO.getMacAddress() + ".");
        }
        if (gestionSensoresRepository.findByIdSensor(sensorDTO.getIdSensor()).isPresent()) {
            throw new GestionSensoresException("Ya hay un sensor con el ID: " + sensorDTO.getIdSensor() + ".");
        }
        // Validar que el idInvernadero exista
        obtenerInvernaderoPorId(new ObjectId(sensorDTO.getIdInvernadero()));
        // Convertir DTO a entidad y guardar
        Sensor sensorEntidad = convertirSensorDTOEntidad(sensorDTO);
        Sensor resultado;
        try {
            resultado = gestionSensoresRepository.save(sensorEntidad);
        } catch (MongoWriteException mwe) {
            throw new GestionSensoresException("Error al guardar el sensor: " + mwe.getMessage());
        }
        // Convertir entidad a DTO y devolver
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
        Optional<Sensor> sensorObtenido = gestionSensoresRepository.findById(new ObjectId(sensorDTO.get_id()));
        if (sensorObtenido.isPresent()) {
            // Actualizar campos del sensor
            Sensor sensorEntidad = sensorObtenido.get();
            sensorEntidad.setIdSensor(sensorDTO.getIdSensor());
            sensorEntidad.setMacAddress(sensorDTO.getMacAddress());
            sensorEntidad.setMarca(sensorDTO.getMarca());
            sensorEntidad.setModelo(sensorDTO.getModelo());
            sensorEntidad.setMagnitud(sensorDTO.getMagnitud());
            sensorEntidad.setUnidad(sensorDTO.getUnidad());
            // Validar que el idInvernadero exista
            obtenerInvernaderoPorId(new ObjectId(sensorDTO.getIdInvernadero()));
            sensorEntidad.setIdInvernadero(new ObjectId(sensorDTO.getIdInvernadero()));
            sensorEntidad.setSector(sensorDTO.getSector());
            sensorEntidad.setFila(sensorDTO.getFila());
            sensorEntidad.setEstado(sensorDTO.isEstado());
            // Guardar sensor actualizado
            Sensor resultado;
            try {
                resultado = gestionSensoresRepository.save(sensorEntidad);
            } catch (MongoWriteException mwe) {
                throw new GestionSensoresException("Ya hay un sensor con la dirección MAC: " + sensorDTO.getMacAddress() + ".");
            }
            // Convertir entidad a DTO y devolver
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
        Optional<Sensor> sensorObtenido = gestionSensoresRepository.findByIdSensor(id);
        if (sensorObtenido.isPresent()) {
            gestionSensoresRepository.delete(sensorObtenido.get());
        } else {
            throw new GestionSensoresException("Sensor con ID " + id + " no encontrado.");
        }
    }

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
                sensorEntidad.getMagnitud(),
                sensorEntidad.getUnidad(),
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
    public Sensor convertirSensorDTOEntidad(SensorDTO sensorDTO) {
        return new Sensor(
                sensorDTO.getIdSensor(),
                sensorDTO.getMacAddress(),
                sensorDTO.getMarca(),
                sensorDTO.getModelo(),
                sensorDTO.getMagnitud(),
                sensorDTO.getUnidad(),
                new ObjectId(sensorDTO.getIdInvernadero()),
                sensorDTO.getSector(),
                sensorDTO.getFila()
        );
    }

    /**
     * Método que convierte una lista de invernaderos de tipo Entidad a tipo DTO.
     *
     * @param invernaderosColeccion Lista Entidad a convertir.
     * @return La lista de invernaderos de tipo DTO.
     */
    public List<InvernaderoDTO> convertirInvernaderosColeccionDTO(List<Invernadero> invernaderosColeccion) {
        List<InvernaderoDTO> invernaderosDTO = new ArrayList<>();
        for (Invernadero invernaderoColeccion : invernaderosColeccion) {
            invernaderosDTO.add(convertirInvernaderoColeccionDTO(invernaderoColeccion));
        }
        return invernaderosDTO;
    }

    /**
     * Método que convierte invernadero de tipo Entidad a tipo DTO.
     *
     * @param invernaderoEntidad Invernadero Entidad a convertir.
     * @return El invernadero de tipo DTO.
     */
    public InvernaderoDTO convertirInvernaderoColeccionDTO(Invernadero invernaderoEntidad) {
        return new InvernaderoDTO(
                invernaderoEntidad.get_id().toString(),
                invernaderoEntidad.getNombre(),
                invernaderoEntidad.getSectores(),
                invernaderoEntidad.getFilas()
        );
    }

    /**
     * Método para obtener un invernadero dado su nombre.
     *
     * @param nombreInvernadero El nombre del invernadero a buscar.
     * @return El invernadero encontrado.
     * @throws GestionSensoresException En caso de que no se haya encontrado nada.
     */
    public Invernadero obtenerInvernaderoPorNombre(String nombreInvernadero) throws GestionSensoresException {
        Optional<Invernadero> invernadero = invernaderosRepository.findByNombre(nombreInvernadero);
        if (invernadero.isPresent()) {
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
        if (invernadero.isPresent()) {
            return invernadero.get();
        } else {
            throw new GestionSensoresException("No se encontró el invernadero con ID: " + idInvernadero.toString());
        }
    }

    /**
     * Método que obtiene todos los invernaderos.
     *
     * @return Una lista con todos los invernaderos encontrados.
     * @throws GestionSensoresException En caso de que no se haya encontrado ningún invernadero.
     */
    public List<InvernaderoDTO> obtenerTodosInvernaderos() throws GestionSensoresException {
        List<Invernadero> invernaderosColeccion = invernaderosRepository.findAll();
        if (!invernaderosColeccion.isEmpty()) {
            return convertirInvernaderosColeccionDTO(invernaderosColeccion);
        } else {
            throw new GestionSensoresException("No se encontró ningún invernadero.");
        }
    }

    /**
     * Método que obtiene todos los sensores asociados a un invernadero dado su ID.
     *
     * @param idInvernadero ID del invernadero cuyos sensores se desean obtener.
     * @return Una lista con los sensores encontrados.
     * @throws GestionSensoresException si no se encuentra el invernadero o no hay sensores asociados.
     */
    public List<SensorDTO> obtenerSensoresPorInvernadero(String idInvernadero) throws GestionSensoresException {
        obtenerInvernaderoPorId(new ObjectId(idInvernadero));
        List<Sensor> sensoresEntidad = gestionSensoresRepository.findByIdInvernadero(new ObjectId(idInvernadero));
        if (!sensoresEntidad.isEmpty()) {
            return convertirSensoresEntidadDTO(sensoresEntidad);
        } else {
            throw new GestionSensoresException("No se encontraron sensores para el invernadero con ID " + idInvernadero + ".");
        }
    }

    /**
     * Obtiene una lista simplificada (ID como String y Nombre) de todos los invernaderos
     * registrados, ideal para poblar selectores en el frontend.
     *
     * @return Lista de InvernaderoBasicoDTO.
     * @throws GestionSensoresException Si ocurre un error al consultar la base de datos.
     */
    public List<InvernaderoBasicoDTO> obtenerTodosLosInvernaderosBasicos() throws GestionSensoresException {
        try {
            List<Invernadero> listaEntidades = invernaderosRepository.findAll();

            List<InvernaderoBasicoDTO> listaDtos = listaEntidades.stream()
                    .map(entidad -> new InvernaderoBasicoDTO(
                            entidad.get_id(),
                            entidad.getNombre()
                    ))
                    .collect(Collectors.toList());

            return listaDtos;
        } catch (Exception e) {
            System.err.println("Error al obtener invernaderos básicos: " + e.getMessage());
            throw new GestionSensoresException("No se pudieron obtener los invernaderos.");
        }
    }

    public List<DatosFaltantesDTO> obtenerDatosSensores(List<String> idsSensores) {
        // Mapa que actúa como caché: idSensor → List(sector, fila)
        Map<String, List<String>> datosSensores = new HashMap<>();

        // Mapa para evitar duplicados: idSensor → DatosFaltantesDTO
        Map<String, DatosFaltantesDTO> datosFaltantesUnicos = new HashMap<>();

        for (String idSensor : idsSensores) {
            // Solo procesamos si no lo hemos visto antes
            if (!datosFaltantesUnicos.containsKey(idSensor)) {
                List<String> datos;

                if (datosSensores.containsKey(idSensor)) {
                    datos = datosSensores.get(idSensor);
                } else {
                    datos = obtenerDatosFaltantes(idSensor);
                    datosSensores.put(idSensor, datos);
                }

                DatosFaltantesDTO dto = new DatosFaltantesDTO(idSensor);
                dto.setSector(datos.get(0));
                dto.setFila(datos.get(datos.size() - 1));
                datosFaltantesUnicos.put(idSensor, dto);
            }
        }
        // Devolvemos solo los datos únicos
        return new ArrayList<>(datosFaltantesUnicos.values());
    }

    private List<String> obtenerDatosFaltantes(String idSensor) {
        System.out.println("[GestionSensoresService DEBUG] Buscando datos faltantes para idSensor: " + idSensor); // Log ID de entrada
        Optional<Sensor> sensorOpt = gestionSensoresRepository.findByIdSensor(idSensor);
        if (sensorOpt.isPresent()) {
            Sensor sensor = sensorOpt.get();
            String sector = sensor.getSector();
            String fila = sensor.getFila();
            System.out.println("[GestionSensoresService DEBUG] Sensor encontrado: ID=" + sensor.getIdSensor() + ", SectorLeido=" + sector + ", FilaLeida=" + fila); // Log datos leídos

            String sectorAEnviar = sector != null ? sector : "";
            String filaAEnviar = fila != null ? fila : "";
            System.out.println("[GestionSensoresService DEBUG] Enviando para ID=" + idSensor + ": Sector='" + sectorAEnviar + "', Fila='" + filaAEnviar + "'"); // Log datos a enviar
            return Arrays.asList(sectorAEnviar, filaAEnviar);
        } else {
            System.out.println("[GestionSensoresService WARN] Sensor con ID '" + idSensor + "' no encontrado. Devolviendo datos vacíos para sector y fila.");
            return Arrays.asList("", "");
        }
    }
}