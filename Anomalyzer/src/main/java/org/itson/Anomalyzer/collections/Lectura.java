package org.itson.Anomalyzer.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.Anomalyzer.dtos.LecturaDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "lecturas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lectura implements Serializable {
    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private float valor;
    private Date fechaHora;
    private ObjectId idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;

    public Lectura(LecturaDTO lecturaEnriquecida) {
        this.idSensor = lecturaEnriquecida.getIdSensor();
        this.macAddress = lecturaEnriquecida.getMacAddress();
        this.marca = lecturaEnriquecida.getMarca();
        this.modelo = lecturaEnriquecida.getModelo();
        this.magnitud = lecturaEnriquecida.getMagnitud();
        this.unidad = lecturaEnriquecida.getUnidad();
        this.valor = lecturaEnriquecida.getValor();
        this.fechaHora = lecturaEnriquecida.getFechaHora();
        this.idInvernadero = new ObjectId(lecturaEnriquecida.getIdInvernadero());
        this.nombreInvernadero = lecturaEnriquecida.getNombreInvernadero();
        this.sector = lecturaEnriquecida.getSector();
        this.fila = lecturaEnriquecida.getFila();
    }
}
