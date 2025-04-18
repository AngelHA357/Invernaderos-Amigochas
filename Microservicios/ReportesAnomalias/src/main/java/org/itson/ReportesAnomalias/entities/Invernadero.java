package org.itson.ReportesAnomalias.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Clase entidad que representa un invernadero.
 */
@Data
@NoArgsConstructor
public class Invernadero {

    private ObjectId _id;

    private String nombre;

    private List<Sensor> sensores;

    public Invernadero(String nombre, List<Sensor> sensores) {
        this.nombre = nombre;
        this.sensores = sensores;
    }

}
