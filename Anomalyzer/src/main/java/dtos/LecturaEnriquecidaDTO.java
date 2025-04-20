package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturaEnriquecidaDTO {
    private double valor;
    private String unidad;
    private Date fecha;
    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String tipoSensor;
    private String magnitud;
    private String idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;
    private boolean estado;
}