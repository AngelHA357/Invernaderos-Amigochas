package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

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
