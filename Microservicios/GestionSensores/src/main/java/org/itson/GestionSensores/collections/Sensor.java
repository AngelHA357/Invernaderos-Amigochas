package org.itson.GestionSensores.collections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Clase entidad que representa un sensor.
 */
@Document(collection = "sensores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    private ObjectId _id;
    @Indexed(unique = true)
    private String idSensor;
    @Indexed(unique = true)
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private ObjectId idInvernadero;
    private String sector;
    private String fila;

    public Sensor(String idSensor, String macAddress, String marca, String modelo, String magnitud, String unidad, ObjectId idInvernadero, String sector, String fila) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.magnitud = magnitud;
        this.unidad = unidad;
        this.idInvernadero = idInvernadero;
        this.sector = sector;
        this.fila = fila;
    }
}
