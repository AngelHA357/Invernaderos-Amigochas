package org.itson.ReportesAnomalias.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase entidad que representa un sensor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    private String id;

    private String macAddress;

    private String marca;

    private String modelo;

    private Invernadero invernadero;

}
