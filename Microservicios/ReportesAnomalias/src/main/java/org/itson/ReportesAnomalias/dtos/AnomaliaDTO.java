package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnomaliaDTO extends LecturaDTO {
    private String causa;

    public AnomaliaDTO(String idSensor, String macAddress, String marca, String modelo, String magnitud, String unidad, float valor, Date fechaHora, String idInvernadero, String nombreInvernadero, String sector, String fila, String causa) {
        super(idSensor, macAddress, marca, modelo, magnitud, unidad, valor, fechaHora, idInvernadero, nombreInvernadero, sector, fila);
        this.causa = causa;
    }
}
