package org.itson.GestionSensores.dtos;

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

    public SensorDTO(String macAddress, String marca, String modelo, String invernadero) {
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.invernadero = invernadero;
    }
}
