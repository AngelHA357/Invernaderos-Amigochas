package org.itson.ReportesAnomalias.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lectura implements Serializable {
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
}
