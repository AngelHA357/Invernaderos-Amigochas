package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomaliaDTO {

    private String codigo;
    private Calendar fechaHora;
    private String causa;
    private String invernadero;
    private String sensor;

}
