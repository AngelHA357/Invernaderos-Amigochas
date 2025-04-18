package org.itson.ReportesAnomalias.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Usuario {

    private ObjectId _id;

    private String nombreUsuario;

    private String contrasenia;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    public Usuario(String nombreUsuario, String contrasenia, String nombre, String apellidoPaterno) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
    }

}
