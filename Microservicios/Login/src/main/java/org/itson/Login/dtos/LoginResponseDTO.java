package org.itson.Login.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String mensaje;
    private String token;
    
    public LoginResponseDTO(String mensaje) {
        this.mensaje = mensaje;
    }
}
