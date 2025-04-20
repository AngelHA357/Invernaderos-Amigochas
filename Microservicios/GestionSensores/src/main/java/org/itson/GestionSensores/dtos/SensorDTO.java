package org.itson.GestionSensores.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * Clase DTO que representa un sensor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO {
    private String _id;
    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private String idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;
    private boolean estado;

    public SensorDTO(String idSensor, String macAddress, String marca, String modelo, String magnitud, String unidad, String idInvernadero, String sector, String fila) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.magnitud = magnitud;
        this.unidad = unidad;
        this.idInvernadero = idInvernadero;
        this.sector = sector;
        this.fila = fila;
        this.estado = true;
    }

    public SensorDTO(String idSensor, String macAddress, String marca, String modelo, String magnitud, String unidad, String idInvernadero, String nombreInvernadero, String sector, String fila, boolean estado) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.magnitud = magnitud;
        this.unidad = unidad;
        this.idInvernadero = idInvernadero;
        this.nombreInvernadero = nombreInvernadero;
        this.sector = sector;
        this.fila = fila;
        this.estado = estado;
    }
}
