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
            System.out.println("Iniciando carga de datos de ejemplo para alarmas...");

            // Verificamos si ya existen alarmas en la base de datos
            long countAlarmas = alarmasRepository.count();
            System.out.println("Alarmas encontradas: " + countAlarmas);

            // Solo insertamos alarmas si no existen
            if (countAlarmas == 0) {
                System.out.println("Creando nuevas alarmas...");

                // Insertar alarmas con IDs de los nuevos sensores
                alarmasRepository.save(new Alarma(
                        "ALM-001",
                        "Humedad",
                        Arrays.asList("SEN-A02"), // Sensor de Humedad del Invernadero A
                        "Invernadero A",
                        40.0f,
                        80.0f,
                        "%",
                        "EMAIL",
                        true
                ));

                alarmasRepository.save(new Alarma(
                        "ALM-002",
                        "Temperatura",
                        Arrays.asList("SEN-A01"), // Sensor de Temperatura del Invernadero A
                        "Invernadero A",
                        15.0f,
                        30.0f,
                        "°C",
                        "SMS",
                        true
                ));

                alarmasRepository.save(new Alarma(
                        "ALM-003",
                        "Temperatura",
                        Arrays.asList("SEN-C01"), // Sensor de Temperatura del Invernadero C
                        "Invernadero C",
                        18.0f,
                        28.0f,
                        "°C",
                        "EMAIL",
                        true
                ));

                alarmasRepository.save(new Alarma(
                        "ALM-004",
                        "Humedad",
                        Arrays.asList("SEN-D02"), // Sensor de Humedad del Invernadero D
                        "Invernadero D",
                        50.0f,
                        75.0f,
                        "%",
                        "SMS",
                        true
                ));

                alarmasRepository.save(new Alarma(
                        "ALM-005",
                        "Temperatura",
                        Arrays.asList("SEN-E01"), // Sensor de Temperatura del Invernadero E
                        "Invernadero E",
                        20.0f,
                        32.0f,
                        "°C",
                        "EMAIL",
                        true
                ));

                System.out.println("Se han insertado 5 alarmas con IDs de sensores actualizados.");
            } else {
                System.out.println("Las alarmas ya existen, no se insertaron datos nuevos.");
            }
        };
    }
}

