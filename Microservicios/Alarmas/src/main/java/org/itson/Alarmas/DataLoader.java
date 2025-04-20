package org.itson.Alarmas;

import org.bson.types.ObjectId;
import org.itson.Alarmas.collections.Alarma;
import org.itson.Alarmas.persistence.IAlarmasRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader {

    @Bean
    CommandLineRunner initData(IAlarmasRepository alarmasRepository) {
        return args -> {
            // Eliminar todas las alarmas existentes
            alarmasRepository.deleteAll();

            // Insertar alarmas
            alarmasRepository.save(new Alarma(
                    "ALM-001",
                    "Humedad",
                    Arrays.asList("SEN-0101"),
                    "Invernadero A",
                    15.0f,
                    16.0f,
                    "%",
                    "Email",
                    true
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-002",
                    "Humedad",
                    Arrays.asList("SEN-0202"),
                    "Invernadero A",
                    40.0f,
                    80.0f,
                    "%",
                    "SMS",
                    true
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-003",
                    "Temperatura",
                    Arrays.asList("SEN-0303"),
                    "Invernadero C",
                    10.0f,
                    25.0f,
                    "F",
                    "Email",
                    false
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-004",
                    "Temperatura",
                    Arrays.asList("SEN-0404"),
                    "Invernadero D",
                    30.0f,
                    70.0f,
                    "K",
                    "SMS",
                    true
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-005",
                    "Humedad",
                    Arrays.asList("SEN-0404"),
                    "Invernadero E",
                    100.0f,
                    1000.0f,
                    "%",
                    "Email",
                    true
            ));

            System.out.println("Se han insertado 5 alarmas con IDs de sensores reales.");
        };
    }
}