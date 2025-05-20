package org.itson.ReportesAnomalias.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "anomalias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anomalia {
    @Id
    private ObjectId _id;
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
    private String causa;

    public Anomalia(String idSensor, String macAddress, String marca, String modelo, String magnitud, String unidad, float valor, Date fechaHora, String idInvernadero, String nombreInvernadero, String sector, String fila, String causa) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.magnitud = magnitud;
        this.unidad = unidad;
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.idInvernadero = idInvernadero;
        this.nombreInvernadero = nombreInvernadero;
        this.sector = sector;
        this.fila = fila;
        this.causa = causa;
    }
}
