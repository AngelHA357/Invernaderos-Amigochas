package org.itson.Lecturas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO que representa una lectura.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturaDTO {
    private String idLectura;
    private String idSensor;
    private String tipoSensor;
    private double valor;
    private String timestamp;
}
