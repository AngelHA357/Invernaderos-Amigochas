package org.itson.ReportesAnomalias.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anomalia {

    private ObjectId id;
    private String codigo;
    private Calendar fechaHora;
    private String causa;
    private String invernadero;
    private String sensor;

    public Anomalia(String codigo, Calendar fechaHora, String causa, String invernadero, String sensor) {
        this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.causa = causa;
        this.invernadero = invernadero;
        this.sensor = sensor;
    }

}
