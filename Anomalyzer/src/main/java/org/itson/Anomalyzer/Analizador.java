package org.itson.Anomalyzer;

import dtos.AlarmaDTO;
import dtos.LecturaDTO;
import org.bson.types.ObjectId;
import org.itson.Alarma.Alarmas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proto.ClienteAlarmasGrpc;

import java.util.ArrayList;
import java.util.List;

@Component
public class Analizador {
    List<AlarmaDTO> alarmas = new ArrayList<>();

    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    public Analizador() {
        List<Alarmas.AlarmaDTO> alarmas = clienteAlarmasGrpc.obtenerAlarmas();

        for (Alarmas.AlarmaDTO alarmasDTO : alarmas) {
            List<String> idSensoresString = alarmasDTO.getIdSensoresList();
            List<ObjectId> idSensoresObjectId = new ArrayList<>();
            for (String idSensor : idSensoresString) {
                idSensoresObjectId.add(new ObjectId(idSensor));
            }
            AlarmaDTO alarmaDTO = new AlarmaDTO();
            alarmaDTO.setIdAlarma(alarmasDTO.getIdAlarma());
            alarmaDTO.setSensores(idSensoresObjectId);
            alarmaDTO.setInvernadero(alarmasDTO.getInvernadero());
            alarmaDTO.setValorMinimo(alarmasDTO.getValorMinimo());
            alarmaDTO.setValorMaximo(alarmasDTO.getValorMaximo());
            alarmaDTO.setActivo(alarmasDTO.getActivo());
            actualizarAlarma(alarmaDTO);
        }
    }

    public void procesarLectura(LecturaDTO lecturaEnriquecida) {
        try {
            // Aquí implementa la lógica de procesamiento para las lecturas enriquecidas
            System.out.println("Procesando lectura enriquecida:");
            System.out.println("ID Sensor: " + lecturaEnriquecida.getIdSensor());
            System.out.println("MAC Address: " + lecturaEnriquecida.getMacAddress());
            System.out.println("Marca: " + lecturaEnriquecida.getMarca());
            System.out.println("Modelo: " + lecturaEnriquecida.getModelo());
            System.out.println("Tipo: " + lecturaEnriquecida.getTipoLectura());
            System.out.println("Magnitud: " + lecturaEnriquecida.getMagnitud());
            System.out.println("Invernadero: " + lecturaEnriquecida.getInvernadero());
            System.out.println("Sector: " + lecturaEnriquecida.getSector());
            System.out.println("Fila: " + lecturaEnriquecida.getFila());
            System.out.println("Valor: " + lecturaEnriquecida.getValor());
            System.out.println("Timestamp: " + lecturaEnriquecida.getFechaHora());

            // Ejemplo: guardar en base de datos, notificar, analizar, etc.

        } catch (Exception e) {
            System.err.println("Error al procesar lectura enriquecida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void agregarAlarma(AlarmaDTO alarma) {
        alarmas.add(alarma);
    }

    public void actualizarAlarma(AlarmaDTO alarmaActualizada) {
        for (AlarmaDTO alarma : alarmas) {
            if (alarma.getIdAlarma().equals(alarmaActualizada.getIdAlarma())) {
                alarma.setSensores(alarmaActualizada.getSensores());
                alarma.setInvernadero(alarmaActualizada.getInvernadero());
                alarma.setValorMinimo(alarmaActualizada.getValorMinimo());
                alarma.setValorMaximo(alarmaActualizada.getValorMaximo());
                alarma.setActivo(alarmaActualizada.isActivo());
                break;
            }
        }
    }

    public void eliminarAlarma(AlarmaDTO alarmaEliminar) {
        for (AlarmaDTO alarma : alarmas) {
            if (alarma.getIdAlarma().equals(alarmaEliminar.getIdAlarma())) {
                alarmas.remove(alarma);
                break;
            }
        }
    }

    public void actualizarAlarmas(List<AlarmaDTO> alarmasDTO) {
        for (AlarmaDTO nuevaAlarma : alarmasDTO) {
            boolean encontrada = false;

            for (AlarmaDTO alarmaExistente : alarmas) {
                if (alarmaExistente.getIdAlarma().equals(nuevaAlarma.getIdAlarma())) {
                    // Actualizar los campos de la alarma existente
                    actualizarAlarma(nuevaAlarma);
                    encontrada = true;
                    break;
                }
            }

            if (!encontrada) {
                // Si no existe, se agrega la nueva alarma
                agregarAlarma(nuevaAlarma);
            }
        }
    }
}
