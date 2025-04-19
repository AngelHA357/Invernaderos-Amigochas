package org.itson.ReportesAnomalias.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;

@Data
@NoArgsConstructor
public class Anomalia {

    private ObjectId id;
    private Calendar fechaHora;
    private String causa;
    private String invernadero;
    private String sensor;

    public Anomalia(Calendar fechaHora, String causa, String invernadero, String sensor) {
        this.fechaHora = fechaHora;
        this.causa = causa;
        this.invernadero = invernadero;
        this.sensor = sensor;
    }

}
