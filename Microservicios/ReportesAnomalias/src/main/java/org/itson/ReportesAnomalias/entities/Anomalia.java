package org.itson.ReportesAnomalias.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Document(collection = "anomalias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anomalia {
    @Id
    private ObjectId id;
    private Date fechaHora;
    private String causa;
    private String invernadero;
    private String sensor;
    private String sector;
    private String fila;

    public Anomalia(Date fechaHora, String causa, String invernadero, String sensor) {
        //this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.causa = causa;
        this.invernadero = invernadero;
        this.sensor = sensor;
    }

}
