package org.itson.Login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.itson.Login.persistence.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    /**
     * Define el Bean para el codificador de contraseñas.
     * Usamos BCrypt, que es el estándar recomendado y seguro.
     * Spring Security lo usará automáticamente para comparar contraseñas.
     * @return Una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el Bean para obtener los detalles del usuario desde la BD.
     */    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            System.out.println("Buscando usuario por username: " + username);
            
            return usuarioRepository.findByUsername(username)
                    .map(usuario -> {
                        // Usar el rol específico del usuario o asignar USER como predeterminado
                        String role = usuario.getRole() != null && !usuario.getRole().isEmpty() 
                                    ? usuario.getRole() 
                                    : "USER";
                        
                        System.out.println("Usuario encontrado: " + usuario.getUsername());
                        System.out.println("Rol del usuario: " + role);
                        System.out.println("Password (hash): " + usuario.getPassword().substring(0, 10) + "...");
                        
                        return new User(
                                usuario.getUsername(),
                                usuario.getPassword(),
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                    })
                    .orElseThrow(() -> {
                        System.out.println("Usuario NO encontrado: " + username);
                        return new UsernameNotFoundException("Usuario no encontrado: " + username);
                    });
        };
    }

    /**
     * Define el Bean del AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }    /**
     * Define la cadena de filtros de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}