package org.itson.Informes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatosFaltantesDTO {
    private String idSensor;
    private String sector;
    private String fila;

    public DatosFaltantesDTO(String idSensor) {
        this.idSensor = idSensor;
    }
}
