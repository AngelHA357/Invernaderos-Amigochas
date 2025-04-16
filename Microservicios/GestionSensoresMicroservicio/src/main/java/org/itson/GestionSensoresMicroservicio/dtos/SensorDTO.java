package org.itson.GestionSensoresMicroservicio.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO que representa un sensor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO {
    private Long id;
    private String macAddress;
    private String marca;
    private String modelo;
    private String invernadero;
}
