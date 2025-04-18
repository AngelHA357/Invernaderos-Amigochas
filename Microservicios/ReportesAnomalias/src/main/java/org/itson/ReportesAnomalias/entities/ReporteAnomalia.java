package org.itson.ReportesAnomalias.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;

@Data
@NoArgsConstructor
public class ReporteAnomalia {

    private ObjectId _id;

    private Calendar fecha;

    private String descripcion;

    private Anomalia anomalia;

    private Usuario usuario;

    public ReporteAnomalia(Calendar fecha, String descripcion, Anomalia anomalia, Usuario usuario) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.anomalia = anomalia;
        this.usuario = usuario;
    }
}
