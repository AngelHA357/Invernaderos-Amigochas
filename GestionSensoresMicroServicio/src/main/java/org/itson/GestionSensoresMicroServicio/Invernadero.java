package org.itson.GestionSensoresMicroservicio;

import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase entidad que representa un invernadero.
 */
@Entity(name = "invernaderos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invernadero {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;

    @OneToMany(mappedBy = "invernadero", cascade = CascadeType.ALL)
    private List<Sensor> sensores;
}
