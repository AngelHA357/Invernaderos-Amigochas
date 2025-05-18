package org.itson.Autenticacion.controlador;

import io.jsonwebtoken.Claims;
import org.itson.Autenticacion.servicio.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/validar")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Comprobar que el header de autenticación existe y tiene el formato correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Token no proporcionado o formato inválido"));
        }

        // Extraer el token
        String token = authHeader.substring(7);

        try {
            // Validar token
            Claims claims = jwtService.validateToken(token);

            // Regresar información del usuario
            Map<String, Object> response = new HashMap<>();
            response.put("userId", claims.getSubject());
            response.put("role", claims.get("role", String.class));
            response.put("valid", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Token inválido: " + e.getMessage()));
        }
    }

    @PostMapping("/proxy/{service}/**")
    public ResponseEntity<?> proxyRequest(
            @PathVariable("service") String service,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) Object body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        // Comprobar que el header de autenticación existe y tiene el formato correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Token no proporcionado o formato inválido"));
        }

        // Extraer token
        String token = authHeader.substring(7);

        try {
            // Validar token
            Claims claims = jwtService.validateToken(token);

            // Enviar la solicitud al servicio correspondiente
            String serviceUrl = "http://" + service + request.getRequestURI().replace("/auth/proxy/" + service, "");

            // Crear los headers para la solicitud
            HttpHeaders forwardHeaders = new HttpHeaders();
            headers.forEach((key, value) -> {
                if (!key.equalsIgnoreCase("host")) {
                    forwardHeaders.add(key, value);
                }
            });

            // Añadir información del usuario
            forwardHeaders.add("X-User-Id", claims.getSubject());
            forwardHeaders.add("X-User-Role", claims.get("role", String.class));


            HttpEntity<?> requestEntity = new HttpEntity<>(body, forwardHeaders);

            // Enviar la petición
            return restTemplate.exchange(serviceUrl, HttpMethod.valueOf(request.getMethod()),
                    requestEntity, Object.class);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Token inválido: " + e.getMessage()));
        }
    }
}
