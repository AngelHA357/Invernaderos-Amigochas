package org.itson.Lecturas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LecturasApplication {
    public static void main(String[] args) {
        // Inicia el servidor
        SpringApplication.run(LecturasApplication.class, args);
    }
}
