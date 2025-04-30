package org.itson.Anomalyzer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.Anomalyzer.collections.Lectura;

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
    private String idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;

    public LecturaDTO(Lectura lectura) {
        this.idSensor = lectura.getIdSensor();
        this.macAddress = lectura.getMacAddress();
        this.marca = lectura.getMarca();
        this.modelo = lectura.getModelo();
        this.magnitud = lectura.getMagnitud();
        this.unidad = lectura.getUnidad();
        this.valor = lectura.getValor();
        this.fechaHora = lectura.getFechaHora();
        this.idInvernadero = lectura.getIdInvernadero().toHexString();
        this.nombreInvernadero = lectura.getNombreInvernadero();
        this.sector = lectura.getSector();
        this.fila = lectura.getFila();
    }
}
