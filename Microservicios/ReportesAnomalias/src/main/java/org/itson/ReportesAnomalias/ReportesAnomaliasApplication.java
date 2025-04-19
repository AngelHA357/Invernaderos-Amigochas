package org.itson.ReportesAnomalias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class ReportesAnomaliasApplication {

    public static void main(String[] args) {
        // Inicializa el programa
        ApplicationContext context = SpringApplication.run(ReportesAnomaliasApplication.class, args);
    }

}
