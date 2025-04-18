package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.entities.Invernadero;
import org.itson.ReportesAnomalias.entities.Sensor;

import java.util.Calendar;

@Data
@AllArgsConstructor
public class AnomaliaDTO {

    private Long id;

    private Calendar fechaHora;

    private String causa;

    private String invernadero;

    private String sensor;

}
