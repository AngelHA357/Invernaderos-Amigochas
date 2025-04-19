package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAnomaliaDTO {

    private String codigo;
    private Calendar fecha;
    private String descripcion;
    private AnomaliaDTO anomalia;
    private String usuario;

}
