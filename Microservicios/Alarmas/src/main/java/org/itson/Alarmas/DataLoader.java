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

            // IDs de sensores de ejemplo
            ObjectId sensor1 = new ObjectId();
            ObjectId sensor2 = new ObjectId();
            ObjectId sensor3 = new ObjectId();
            ObjectId sensor4 = new ObjectId();
            ObjectId sensor5 = new ObjectId();

            // Insertar alarmas
            alarmasRepository.save(new Alarma(
                    "ALM-001",
                    "Temperatura",
                    Arrays.asList(sensor1, sensor2),
                    "Invernadero A",
                    15.0f,
                    30.0f,
                    1.0f,
                    "Email",
                    true
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-002",
                    "Humedad",
                    Arrays.asList(sensor2, sensor3),
                    "Invernadero B",
                    40.0f,
                    80.0f,
                    1.0f,
                    "SMS",
                    true
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-003",
                    "Temperatura",
                    Arrays.asList(sensor3, sensor4),
                    "Invernadero C",
                    10.0f,
                    25.0f,
                    1.0f,
                    "Email",
                    false
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-004",
                    "Humedad",
                    Arrays.asList(sensor4, sensor5),
                    "Invernadero D",
                    30.0f,
                    70.0f,
                    1.0f,
                    "SMS",
                    true
            ));

            alarmasRepository.save(new Alarma(
                    "ALM-005",
                    "Luz",
                    Arrays.asList(sensor1, sensor5),
                    "Invernadero E",
                    100.0f,
                    1000.0f,
                    1.0f,
                    "Email",
                    true
            ));

            System.out.println("Se han insertado 5 alarmas");
        };
    }
}