package org.itson.Login.service;

import org.itson.Login.dtos.LoginRequestDTO;
import org.itson.Login.dtos.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  /**
     * Intenta autenticar a un usuario usando el AuthenticationManager de Spring Security.
     * Este manager usará internamente el UserDetailsService (que busca en tu BD)
     * y el PasswordEncoder (BCrypt) que configuramos.
     *
     * @param request DTO con username y password que vienen del Controller.
     * @return DTO de respuesta si la autenticación es exitosa.
     * @throws AuthenticationException Si la autenticación falla (será capturada por el Controller).
     */    
    public LoginResponseDTO login(LoginRequestDTO request) throws AuthenticationException {
        System.out.println("Intentando autenticar usuario: " + request.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // Generar token JWT después de autenticar correctamente
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            System.out.println("Usuario autenticado: " + userDetails.getUsername());
            System.out.println("Autoridades: " + userDetails.getAuthorities());
            
            // Añadir el rol al token, obteniéndolo de las autoridades del usuario
            String role = userDetails.getAuthorities().isEmpty() ? 
                        "USER" : 
                        userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            
            System.out.println("Rol extraído: " + role);
            
            // Crear los claims adicionales con el rol
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);
            
            // Generar el token con los claims adicionales
            String jwtToken = jwtService.generateToken(claims, userDetails);
              System.out.println("Token generado correctamente de longitud: " + jwtToken.length());

            return new LoginResponseDTO("Usuario autenticado exitosamente!", jwtToken);
        } catch (AuthenticationException e) {
            System.out.println("Error de autenticación: " + e.getMessage());
            throw e; // Lanzar la excepción para que el controlador la maneje
        }
    }
}