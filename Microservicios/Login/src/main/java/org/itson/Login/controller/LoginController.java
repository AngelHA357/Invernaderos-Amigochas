package org.itson.Login.controller;

import org.itson.Login.dtos.LoginRequestDTO;
import org.itson.Login.dtos.LoginResponseDTO;
import org.itson.Login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * Endpoint para procesar las solicitudes de login vía POST.
     * Espera un cuerpo JSON con username y password.
     * @param requestDTO Objeto con los datos de login (mapeado desde el JSON).
     * @return ResponseEntity con LoginResponseDTO (éxito) o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequestDTO requestDTO) {
        try {
            LoginResponseDTO response = loginService.login(requestDTO);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error de autenticación: Credenciales inválidas.");
            System.err.println("Intento de login fallido para usuario: " + requestDTO.getUsername() + " - Razón: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno en el servidor durante el login.");
            System.err.println("Error inesperado en login para usuario " + (requestDTO != null ? requestDTO.getUsername() : "??") + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
