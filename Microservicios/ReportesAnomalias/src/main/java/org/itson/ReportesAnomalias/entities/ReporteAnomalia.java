package org.itson.ReportesAnomalias.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteAnomalia {

    private ObjectId id;
    private String codigo;
    private Calendar fecha;
    private String descripcion;
    private Anomalia anomalia;
    private String usuario;

    public ReporteAnomalia(String codigo, Calendar fecha, String descripcion, Anomalia anomalia, String usuario) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.anomalia = anomalia;
        this.usuario = usuario;
    }
}
