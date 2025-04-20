package org.itson.GestionSensores.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvernaderoDTO {
    private String _id;
    private String nombre;
    private List<String> sectores;
    private List<String> filas;
}
