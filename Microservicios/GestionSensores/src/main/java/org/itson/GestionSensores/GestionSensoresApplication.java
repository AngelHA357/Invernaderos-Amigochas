package org.itson.GestionSensores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

/**
 * Clase principal del microservicio.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GestionSensoresApplication {

    public static void main(String[] args) {
        // Inicializa el programa
        ApplicationContext context = SpringApplication.run(GestionSensoresApplication.class, args);
    }

}
