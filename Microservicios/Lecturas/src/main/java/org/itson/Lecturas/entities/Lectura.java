package org.itson.Lecturas.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase entidad que representa un sensor.
 */
@Document(collection = "lecturas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lectura {
    @Id
    private String id;
    private String idSensor;
    private String tipoSensor;
    private double valor;
    private String timestamp;

    public Lectura(String idSensor, String tipoSensor, double valor, String timestamp) {
        this.idSensor = idSensor;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.timestamp = timestamp;
    }
}
