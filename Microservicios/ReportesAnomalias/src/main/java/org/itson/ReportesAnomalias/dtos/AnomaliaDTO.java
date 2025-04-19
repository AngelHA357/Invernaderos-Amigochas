package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomaliaDTO {

    private String id;
    private Date fechaHora;
    private String causa;
    private String invernadero;
    private String sensor;

}
