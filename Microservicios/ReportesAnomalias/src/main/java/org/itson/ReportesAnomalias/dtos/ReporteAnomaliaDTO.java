package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAnomaliaDTO {

    private String id;
    private Date fecha;
    private String acciones;
    private String comentarios;
    private AnomaliaDTO anomalia;
    private String usuario;

}
