package org.itson.ReportesAnomalias.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Document(collection = "reportes_anomalias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteAnomalia {
    @Id
    private ObjectId _id;
    private Date fecha;
    private String acciones;
    private String comentarios;
    private Anomalia anomalia;
    private String usuario;

    public ReporteAnomalia(Date fecha, String acciones, String comentarios, Anomalia anomalia, String usuario) {
        this.fecha = fecha;
        this.acciones = acciones;
        this.comentarios = comentarios;
        this.anomalia = anomalia;
        this.usuario = usuario;
    }
}
