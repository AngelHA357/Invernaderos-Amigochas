package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.entities.Usuario;

import java.util.Calendar;

@Data
@AllArgsConstructor
public class ReporteAnomaliaDTO {

    private Long id;

    private String codigo;

    private Calendar fecha;

    private String descripcion;

    private AnomaliaDTO anomalia;

    private String usuario;
}
