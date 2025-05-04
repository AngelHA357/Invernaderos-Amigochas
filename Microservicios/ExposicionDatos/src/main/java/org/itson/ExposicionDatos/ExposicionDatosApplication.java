package org.itson.ExposicionDatos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class ExposicionDatosApplication {

    public static void main(String[] args) {
        // Inicializa el programa
        ApplicationContext context = SpringApplication.run(ExposicionDatosApplication.class, args);
    }

}
