package org.itson.Alarmator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmaDTO {
    private String idAlarma;
    private List<String> sensores;
    private String invernadero;
    private float valorMinimo;
    private float valorMaximo;
    private String magnitud;
    private String unidad;
    private String medioNotificacion;
    private boolean activo;
}
