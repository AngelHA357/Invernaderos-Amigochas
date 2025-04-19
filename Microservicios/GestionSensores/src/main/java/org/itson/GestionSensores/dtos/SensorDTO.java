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
    private String idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;

    public SensorDTO(String idSensor, String macAddress, String marca, String modelo, String idInvernadero, String sector, String fila) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.idInvernadero = idInvernadero;
        this.sector = sector;
        this.fila = fila;
    }
}
