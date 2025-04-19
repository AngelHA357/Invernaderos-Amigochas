package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomaliaDTO {

    private String id;
    private Date fechaHora;
    private String causa;
    private String invernadero;
    private String magnitud;
    private float valor;
    private String idSensor;
    private String sector;
    private String fila;

    public AnomaliaDTO(Date fechaHora, String causa, String invernadero, String magnitud, float valor, String idSensor, String sector, String fila) {
        this.fechaHora = fechaHora;
        this.causa = causa;
        this.invernadero = invernadero;
        this.magnitud = magnitud;
        this.valor = valor;
        this.idSensor = idSensor;
        this.sector = sector;
        this.fila = fila;
    }
}
