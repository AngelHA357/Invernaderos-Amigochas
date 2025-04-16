package org.itson.GestionSensoresMicroservicio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase entidad que representa un sensor.
 */
@Entity(name = "sensores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    @GeneratedValue
    private Long id;
    private String macAddress;
    private String marca;
    private String modelo;

    @ManyToOne
    @JoinColumn(name = "invernadero_id")
    @JsonIgnore
    private Invernadero invernadero;
}
