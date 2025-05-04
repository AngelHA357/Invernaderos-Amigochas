package org.itson.Alarmator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnomaliaDTO extends LecturaDTO{
    private String causa;

    public AnomaliaDTO(LecturaDTO lectura) {
        super(lectura.getIdSensor(), lectura.getMacAddress(), lectura.getMarca(), lectura.getModelo(),
                lectura.getMagnitud(), lectura.getUnidad(), lectura.getValor(), lectura.getFechaHora(),
                lectura.getIdInvernadero(), lectura.getNombreInvernadero(), lectura.getSector(), lectura.getFila());
    }
}
