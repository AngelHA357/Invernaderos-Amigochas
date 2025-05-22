package org.itson.GestionSensores;

import org.bson.types.ObjectId;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.collections.Sensor;
import org.itson.GestionSensores.persistence.IGestionSensoresRepository;
import org.itson.GestionSensores.persistence.IInvernaderosRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader {

    @Bean
    CommandLineRunner initData(IInvernaderosRepository invernaderosRepository, IGestionSensoresRepository gestionSensoresRepository) {
        return args -> {
            System.out.println("Iniciando carga de datos de ejemplo...");

            // Verificamos si ya existen invernaderos en la base de datos
            long countInvernaderos = invernaderosRepository.count();
            System.out.println("Invernaderos encontrados: " + countInvernaderos);

            // Solo insertamos invernaderos si no existen
            if (countInvernaderos == 0) {
                System.out.println("Creando nuevos invernaderos...");

                // Insertar invernaderos
                Invernadero invA = invernaderosRepository.save(new Invernadero(null, "Invernadero A", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
                Invernadero invB = invernaderosRepository.save(new Invernadero(null, "Invernadero B", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
                Invernadero invC = invernaderosRepository.save(new Invernadero(null, "Invernadero C", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
                Invernadero invD = invernaderosRepository.save(new Invernadero(null, "Invernadero D", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
                Invernadero invE = invernaderosRepository.save(new Invernadero(null, "Invernadero E", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
                System.out.println("Se han insertado 5 invernaderos");

                // Insertar dos sensores por invernadero con datos completos
                // Invernadero A - Sensores
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-A01", "AA:BB:CC:DD:EE:FF", "SensorTech", "ST-100", "Temperatura", "°C", new ObjectId(invA.get_id()), "Sector 1", "Fila A", true));
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-A02", "AA:BB:CC:DD:EE:01", "SensorTech", "ST-101", "Humedad", "%", new ObjectId(invA.get_id()), "Sector 2", "Fila B", true));

                // Invernadero B - Sensores
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-B01", "BB:CC:DD:EE:FF:01", "EcoSense", "ES-200", "Temperatura", "°C", new ObjectId(invB.get_id()), "Sector 1", "Fila A", true));
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-B02", "BB:CC:DD:EE:FF:02", "EcoSense", "ES-201", "Humedad", "%", new ObjectId(invB.get_id()), "Sector 2", "Fila B", true));

                // Invernadero C - Sensores
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-C01", "CC:DD:EE:FF:01:02", "TempTech", "T-300", "Temperatura", "°C", new ObjectId(invC.get_id()), "Sector 1", "Fila A", true));
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-C02", "CC:DD:EE:FF:01:03", "TempTech", "T-301", "Humedad", "%", new ObjectId(invC.get_id()), "Sector 2", "Fila B", true));

                // Invernadero D - Sensores
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-D01", "DD:EE:FF:01:02:03", "GreenGrow", "GG-500", "Temperatura", "°C", new ObjectId(invD.get_id()), "Sector 1", "Fila A", true));
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-D02", "DD:EE:FF:01:02:04", "GreenGrow", "GG-501", "Humedad", "%", new ObjectId(invD.get_id()), "Sector 2", "Fila B", true));

                // Invernadero E - Sensores
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-E01", "EE:FF:01:02:03:04", "AgroSense", "AS-400", "Temperatura", "°C", new ObjectId(invE.get_id()), "Sector 1", "Fila A", true));
                gestionSensoresRepository.save(new Sensor(new ObjectId(), "SEN-E02", "EE:FF:01:02:03:05", "AgroSense", "AS-401", "Humedad", "%", new ObjectId(invE.get_id()), "Sector 2", "Fila B", true));

            // Insertar sensores
            gestionSensoresRepository.save(new Sensor(new ObjectId("661eff4b0000000000000001"), "SEN-0101", "AA:BB:CC:DD:EE:FF", "SensorTech", "ST-100", "Humedad", "%", new ObjectId(invA.get_id()), "Sector 1", "Fila A", true));
            gestionSensoresRepository.save(new Sensor(new ObjectId("661eff4b0000000000000002"),"SEN-0202", "11:22:33:44:55:66", "EcoSense", "ES-200", "Temperatura", "C", new ObjectId(invB.get_id()), "Sector 2", "Fila B", true));
            gestionSensoresRepository.save(new Sensor(new ObjectId("661eff4b0000000000000003"),"SEN-0303", "AA:11:BB:22:CC:33", "TempTech", "T-300", "Temperatura", "F", new ObjectId(invC.get_id()), "Sector 1", "Fila A", true));
            gestionSensoresRepository.save(new Sensor(new ObjectId("661eff4b0000000000000004"),"SEN-0404", "66:55:44:33:22:11", "GreenGrow", "GG-500", "Temperatura", "K", new ObjectId(invD.get_id()), "Sector 1", "Fila A", true));
            gestionSensoresRepository.save(new Sensor(new ObjectId("661eff4b0000000000000005"),"SEN-0505", "77:88:99:AA:BB:CC", "AgroSense", "AS-400", "Humedad", "%", new ObjectId(invE.get_id()), "Sector 2", "Fila B", true));
            System.out.println("Se han insertado 5 sensores");
        };
    }
}