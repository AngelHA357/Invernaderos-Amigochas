package org.itson.GestionSensores.entities;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mac_address", nullable = false, unique = true)
    private String macAddress;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @ManyToOne
    @JoinColumn(name = "invernadero_id")
    @JsonIgnore
    private Invernadero invernadero;

    public Sensor(String macAddress, String marca, String modelo, Invernadero invernadero) {
        this.macAddress = macAddress;
        this.marca = marca;
        this.modelo = modelo;
        this.invernadero = invernadero;
    }
}
