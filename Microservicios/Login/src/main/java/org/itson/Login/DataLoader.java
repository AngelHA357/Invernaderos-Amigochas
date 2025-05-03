package org.itson.Login;

import org.itson.Login.collections.Usuario;
import org.itson.Login.persistence.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;

    //@Bean
    CommandLineRunner initDatabase(IUsuarioRepository usuarioRepository) {

        return args -> {

            try {
                String dbName = mongoTemplate.getDb().getName();
                System.out.println("====> DataLoader está conectado a la BD: '" + dbName + "' <====");
            } catch (Exception e) {
                System.err.println("====> DataLoader: ERROR al obtener nombre de DB: " + e.getMessage() + " <====");
            }

            String testUsername = "usuario";
            String plainPassword = "password";

            if (usuarioRepository.findByUsername(testUsername).isEmpty()) {
                String hashedPassword = passwordEncoder.encode(plainPassword);
                System.out.println("DataLoader: Cifrando contraseña '" + plainPassword + "' como: '" + hashedPassword + "'");
                Usuario testUser = new Usuario();
                testUser.setId(null);
                testUser.setUsername(testUsername);
                testUser.setPassword(hashedPassword);
                usuarioRepository.save(testUser);
                System.out.println(">>> DataLoader: Usuario de prueba '" + testUsername + "' CREADO. Usa contraseña '" + plainPassword + "' para probar login. <<<");
            } else {
                System.out.println(">>> DataLoader: Usuario de prueba '" + testUsername + "' ya existe. No se insertó de nuevo. <<<");
            }
        };
    }
}
