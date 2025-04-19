package org.itson.Lecturas.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Clase entidad que representa un sensor.
 */
@Document(collection = "lecturas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lectura {
    @Id
    private ObjectId _id;
    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String tipoLectura;
    private String magnitud;
    private float valor;
    private Date fechaHora;

    public Lectura(String idSensor, String macAddress, String marca, String modelo, String tipoLectura, String magnitud, float valor, Date fechaHora) {
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.tipoLectura = tipoLectura;
        this.magnitud = magnitud;
        this.valor = valor;
        this.fechaHora = fechaHora;
    }
}
