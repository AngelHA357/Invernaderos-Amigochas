package org.itson.Informes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvernaderoBasicoDTO {
    private String id;       // Ej: "INV-0101"
    private String nombre;   // Ej: "Invernadero A"
}
