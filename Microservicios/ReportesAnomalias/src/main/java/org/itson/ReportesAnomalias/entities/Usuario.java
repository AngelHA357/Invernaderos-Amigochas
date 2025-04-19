package org.itson.ReportesAnomalias.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private String nombreUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;

    public Usuario(String nombreUsuario, String nombre, String apellidoPaterno) {
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
    }

}
