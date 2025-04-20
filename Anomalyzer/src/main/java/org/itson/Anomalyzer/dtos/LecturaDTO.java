package org.itson.Anomalyzer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Clase DTO que representa una lectura.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturaDTO {
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
}
