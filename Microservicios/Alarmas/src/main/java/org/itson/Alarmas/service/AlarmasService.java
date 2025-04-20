package org.itson.Alarmas.service;

import org.itson.Alarmas.collections.Alarma;
import org.itson.Alarmas.dtos.AlarmaDTO;
import org.itson.Alarmas.exceptions.AlarmasException;
import org.itson.Alarmas.proto.ClienteAnomalyzerGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.itson.Alarmas.persistence.IAlarmasRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlarmasService {
    @Autowired
    private IAlarmasRepository alarmasRepository;

    @Autowired
    ClienteAnomalyzerGrpc clienteAnomalyzerGrpc;

    /**
     * Método que obtiene todas las alarmas.
     *
     * @return Una lista con todas las alarmas encontradas.
     * @throws AlarmasException en caso de que no se haya encontrado ninguna alarma.
     */
    public List<AlarmaDTO> obtenerTodasLasAlarmas() throws AlarmasException {
        List<Alarma> alarmasEntidad = alarmasRepository.findAll();
        if (!alarmasEntidad.isEmpty()) {
            List<AlarmaDTO> alarmasDTO = convertirAlarmasEntidadDTO(alarmasEntidad);
            return alarmasDTO;
        } else {
            throw new AlarmasException("No se encontró ninguna alarma.");
        }
    }

    /**
     * Método que obtiene una alarma dado su ID.
     *
     * @param idAlarma ID de la alarma a buscar.
     * @return La alarma encontrada.
     * @throws AlarmasException En caso de que no se haya encontrado nada.
     */
    public AlarmaDTO obtenerAlarmaPorId(String idAlarma) throws AlarmasException {
        Optional<Alarma> alarmaEntidad = alarmasRepository.findByIdAlarma(idAlarma);
        if (alarmaEntidad.isPresent()) {
            AlarmaDTO alarmaDTO = convertirAlarmaEntidadDTO(alarmaEntidad.get());
            return alarmaDTO;
        } else {
            throw new AlarmasException("Alarma con ID " + idAlarma + " no encontrada.");
        }
    }

    /**
     * Método para registrar una nueva alarma.
     *
     * @param alarmaDTO La alarma a registrar.
     * @return La alarma registrada.
     * @throws AlarmasException En caso de que ocurra un error durante el registro.
     */
    public AlarmaDTO registrarAlarma(AlarmaDTO alarmaDTO) throws AlarmasException {
        if (alarmasRepository.findByIdAlarma(alarmaDTO.getIdAlarma()).isPresent()) {
            throw new AlarmasException("Ya existe una alarma con el ID: " + alarmaDTO.getIdAlarma() + ".");
        }
        Alarma alarmaEntidad = convertirAlarmaDTOEntidad(alarmaDTO);
        Alarma resultado = alarmasRepository.save(alarmaEntidad);
        clienteAnomalyzerGrpc.registrarAlarma(convertirAlarmaEntidadDTO(resultado));
        return convertirAlarmaEntidadDTO(resultado);
    }

    /**
     * Método para editar los datos de una alarma.
     *
     * @param alarmaDTO La alarma a editar.
     * @return La alarma editada.
     * @throws AlarmasException En caso de que ocurra un error durante la edición.
     */
    public AlarmaDTO editarAlarma(AlarmaDTO alarmaDTO) throws AlarmasException {
        Optional<Alarma> alarmaObtenida = alarmasRepository.findByIdAlarma(alarmaDTO.getIdAlarma());
        if (alarmaObtenida.isPresent()) {
            Alarma alarmaEntidad = alarmaObtenida.get();
            alarmaEntidad.setMagnitud(alarmaDTO.getMagnitud());
            alarmaEntidad.setSensores(alarmaDTO.getSensores());
            alarmaEntidad.setInvernadero(alarmaDTO.getInvernadero());
            alarmaEntidad.setValorMinimo(alarmaDTO.getValorMinimo());
            alarmaEntidad.setValorMaximo(alarmaDTO.getValorMaximo());
            alarmaEntidad.setUnidad(alarmaDTO.getUnidad());
            alarmaEntidad.setMedioNotificacion(alarmaDTO.getMedioNotificacion());
            alarmaEntidad.setActivo(alarmaDTO.isActivo());
            Alarma resultado = alarmasRepository.save(alarmaEntidad);
            clienteAnomalyzerGrpc.actualizarAlarma(convertirAlarmaEntidadDTO(resultado));
            return convertirAlarmaEntidadDTO(resultado);
        } else {
            throw new AlarmasException("Alarma con ID " + alarmaDTO.getIdAlarma() + " no encontrada.");
        }
    }

    /**
     * Método para eliminar una alarma.
     *
     * @param id ID de la alarma a eliminar.
     * @throws AlarmasException En caso de que ocurra un error durante la eliminación.
     */
    public void eliminarAlarma(String id) throws AlarmasException {
        Optional<Alarma> alarmaObtenida = alarmasRepository.findByIdAlarma(id);
        if (alarmaObtenida.isPresent()) {
            alarmasRepository.delete(alarmaObtenida.get());
            clienteAnomalyzerGrpc.eliminarAlarma(id);
        } else {
            throw new AlarmasException("Alarma con ID " + id + " no encontrada.");
        }
    }

    /**
     * Método que convierte una lista de alarmas de tipo Entidad a tipo DTO.
     *
     * @param alarmasEntidad Lista de alarmas Entidad a convertir.
     * @return La lista de alarmas de tipo DTO.
     */
    public List<AlarmaDTO> convertirAlarmasEntidadDTO(List<Alarma> alarmasEntidad) {
        List<AlarmaDTO> alarmasDTO = new ArrayList<>();
        for (Alarma alarma : alarmasEntidad) {
            alarmasDTO.add(convertirAlarmaEntidadDTO(alarma));
        }
        return alarmasDTO;
    }

    /**
     * Método que convierte una alarma de tipo Entidad a tipo DTO.
     *
     * @param alarmaEntidad Alarma Entidad a convertir.
     * @return La alarma de tipo DTO.
     */
    public AlarmaDTO convertirAlarmaEntidadDTO(Alarma alarmaEntidad) throws AlarmasException {
        return new AlarmaDTO(
                alarmaEntidad.getIdAlarma(),
                alarmaEntidad.getMagnitud(),
                alarmaEntidad.getSensores(),
                alarmaEntidad.getInvernadero(),
                alarmaEntidad.getValorMinimo(),
                alarmaEntidad.getValorMaximo(),
                alarmaEntidad.getUnidad(),
                alarmaEntidad.getMedioNotificacion(),
                alarmaEntidad.isActivo()
        );
    }

    /**
     * Método que convierte una alarma de tipo DTO a tipo Entidad.
     *
     * @param alarmaDTO Alarma DTO a convertir.
     * @return La alarma de tipo Entidad.
     */
    public Alarma convertirAlarmaDTOEntidad(AlarmaDTO alarmaDTO) throws AlarmasException {
        return new Alarma(
                alarmaDTO.getIdAlarma(),
                alarmaDTO.getMagnitud(),
                alarmaDTO.getSensores(),
                alarmaDTO.getInvernadero(),
                alarmaDTO.getValorMinimo(),
                alarmaDTO.getValorMaximo(),
                alarmaDTO.getUnidad(),
                alarmaDTO.getMedioNotificacion(),
                alarmaDTO.isActivo()
        );
    }
}