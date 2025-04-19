package org.itson.GestionSensores;

import org.bson.types.ObjectId;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.collections.Sensor;
import org.itson.GestionSensores.persistence.IGestionSensoresRepository;
import org.itson.GestionSensores.persistence.IInvernaderosRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader {

    @Bean
    CommandLineRunner initData(IInvernaderosRepository invernaderosRepository, IGestionSensoresRepository gestionSensoresRepository) {
        return args -> {
            invernaderosRepository.deleteAll();
            gestionSensoresRepository.deleteAll();

            // Insertar invernaderos
            Invernadero invA = invernaderosRepository.save(new Invernadero(null, "Invernadero A", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invB = invernaderosRepository.save(new Invernadero(null, "Invernadero B", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invC = invernaderosRepository.save(new Invernadero(null, "Invernadero C", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invE = invernaderosRepository.save(new Invernadero(null, "Invernadero E", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));
            Invernadero invD = invernaderosRepository.save(new Invernadero(null, "Invernadero D", List.of("Sector 1", "Sector 2"), List.of("Fila A", "Fila B")));

            // Insertar sensores
            gestionSensoresRepository.save(new Sensor(null, "AA:BB:CC:DD:EE:FF", "SensorTech", "ST-100", new ObjectId(invA.get_id()), "Sector 1", "Fila A"));
            gestionSensoresRepository.save(new Sensor(null, "11:22:33:44:55:66", "EcoSense", "ES-200", new ObjectId(invB.get_id()), "Sector 2", "Fila B"));
            gestionSensoresRepository.save(new Sensor(null, "AA:11:BB:22:CC:33", "TempTech", "T-300", new ObjectId(invC.get_id()), "Sector 1", "Fila A"));
            gestionSensoresRepository.save(new Sensor(null, "66:55:44:33:22:11", "GreenGrow", "GG-500", new ObjectId(invD.get_id()), "Sector 1", "Fila A"));
            gestionSensoresRepository.save(new Sensor(null, "77:88:99:AA:BB:CC", "AgroSense", "AS-400", new ObjectId(invE.get_id()), "Sector 2", "Fila B"));

        };
    }
}