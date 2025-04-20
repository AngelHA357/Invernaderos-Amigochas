package org.itson.Lecturas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.grpc.SensorLectura;

import java.util.Date;

/**
 * Clase DTO que representa una lectura.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturaDTO {
    private ObjectId _id;
    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private float valor;
    private Date fechaHora;
    private String invernadero;
    private String sector;
    private String fila;
    private boolean estado;

    public SensorLectura toSensorLectura() {
        return SensorLectura.newBuilder()
                .setIdSensor(this.getIdSensor())
                .setMacAddress(this.getMacAddress() != null ? this.getMacAddress() : "")
                .setMarca(this.getMarca() != null ? this.getMarca() : "")
                .setModelo(this.getModelo() != null ? this.getModelo() : "")
                .setMagnitud(this.getMagnitud() != null ? this.getMagnitud() : "")
                .setUnidad(this.getUnidad() != null ? this.getUnidad() : "")
                .build();
    }
}
