package org.itson.ReportesAnomalias.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;

@Data
@NoArgsConstructor
public class Anomalia {

    private ObjectId _id;

    private Calendar fechaHora;

    private String causa;

    private Invernadero invernadero;

    private Sensor sensor;

    public Anomalia(Calendar fechaHora, String causa, Invernadero invernadero, Sensor sensor) {
        this.fechaHora = fechaHora;
        this.causa = causa;
        this.invernadero = invernadero;
        this.sensor = sensor;
    }

}
