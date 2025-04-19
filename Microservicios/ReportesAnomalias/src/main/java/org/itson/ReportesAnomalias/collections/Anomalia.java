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
    private Date fechaHora;
    private String causa;
    private String invernadero;
    private String magnitud;
    private float valor;
    private String sensor;
    private String sector;
    private String fila;

    public Anomalia(Date fechaHora, String causa, String invernadero, String magnitud, float valor, String sensor, String sector, String fila) {
        this.fechaHora = fechaHora;
        this.causa = causa;
        this.invernadero = invernadero;
        this.magnitud = magnitud;
        this.valor = valor;
        this.sensor = sensor;
        this.sector = sector;
        this.fila = fila;
    }
}
