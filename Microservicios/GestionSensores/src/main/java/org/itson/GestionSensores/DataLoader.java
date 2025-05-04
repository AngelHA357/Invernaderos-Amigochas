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
            if(invernaderosRepository.count() > 0) {
                return; // No se hace nada si ya hay datos
            }
            // Insertar invernaderos
            Invernadero invNA = invernaderosRepository.save(new Invernadero(null, "N/A", List.of("N/A"), List.of("N/A")));
            Invernadero invA = invernaderosRepository.save(new Invernadero(null, "Invernadero A", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invB = invernaderosRepository.save(new Invernadero(null, "Invernadero B", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invC = invernaderosRepository.save(new Invernadero(null, "Invernadero C", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invE = invernaderosRepository.save(new Invernadero(null, "Invernadero E", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invD = invernaderosRepository.save(new Invernadero(null, "Invernadero D", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            System.out.println("Se han insertado 6 invernaderos");

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