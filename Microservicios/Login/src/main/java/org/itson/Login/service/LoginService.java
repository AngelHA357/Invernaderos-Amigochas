package org.itson.Login.service;

import org.itson.Login.dtos.LoginRequestDTO;
import org.itson.Login.dtos.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

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

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return new LoginResponseDTO("Usuario autenticado exitosamente!");
    }
}