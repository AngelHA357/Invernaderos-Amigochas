package org.itson.ExposicionDatos.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.Informes.dtos.LecturaDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "lecturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lectura {

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
    private boolean estado;

    public Lectura(String idSensor, String macAddress, String marca, String modelo, String magnitud, String unidad, float valor, Date fechaHora, String idInvernadero, String nombreInvernadero, String sector, String fila, boolean estado) {
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
        this.estado = estado;
    }

}
