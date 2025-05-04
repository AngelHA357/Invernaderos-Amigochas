package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomaliaDTO {

    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private float valor;
    private Date fechaHora;
    private ObjectId idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;
    private String causa;

}
