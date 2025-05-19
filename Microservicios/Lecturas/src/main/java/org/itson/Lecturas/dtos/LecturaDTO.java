package org.itson.Lecturas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * Clase DTO que representa una lectura.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturaDTO {
    private String _id;
    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private float valor;
    private Date fechaHora;
    private String idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;
}
