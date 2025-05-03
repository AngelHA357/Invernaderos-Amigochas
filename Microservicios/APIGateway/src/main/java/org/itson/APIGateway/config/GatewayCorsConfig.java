package org.itson.APIGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Configuration
public class GatewayCorsConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayCorsConfig.class);

    @PostConstruct
    public void init() {
        log.info(">>>>>>>>>>>>>>>> GatewayCorsConfig CARGADA CORRECTAMENTE! <<<<<<<<<<<<<<<<<");
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

    @Bean
    CorsWebFilter corsWebFilter() {
        log.info(">>> CorsWebFilter Bean CREADO.");
        return new CorsWebFilter(corsConfigurationSource());
    }
}
