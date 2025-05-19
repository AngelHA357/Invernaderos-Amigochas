package org.itson.Login.config;

import org.bson.types.ObjectId;
import org.itson.Login.collections.Usuario;
import org.itson.Login.persistence.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;    @Override
    public void run(String... args) throws Exception {
        // Eliminar todos los usuarios para inicializar de forma fresca
        usuarioRepository.deleteAll();
        System.out.println("🗑️ Base de datos de usuarios limpiada.");
            
        // Inicializar los usuarios con roles
        initializeUsers();
        
        // Mostrar todos los usuarios en la base de datos para verificar
        System.out.println("\n🔍 Verificando usuarios en la base de datos:");
        usuarioRepository.findAll().forEach(user -> {
            System.out.println("   Usuario: " + user.getUsername() + 
                             " | Rol: " + user.getRole() + 
                             " | Password (hash): " + user.getPassword().substring(0, 15) + "...");
        });
        
        System.out.println("👤 Total de usuarios: " + usuarioRepository.count());
    }

    private void initializeUsers() {
        // Lista de usuarios con diferentes roles
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(
                        new ObjectId(),
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "ADMIN",
                        "Administrador",
                        "Sistema",
                        "admin@amigochas.com"
                ),
                new Usuario(
                        new ObjectId(),
                        "operador",
                        passwordEncoder.encode("operador123"),
                        "OPERATOR",
                        "Operador",
                        "Sistema",
                        "operador@amigochas.com"
                ),
                new Usuario(
                        new ObjectId(),
                        "analista",
                        passwordEncoder.encode("analista123"),
                        "ANALYST",
                        "Analista",
                        "Datos",
                        "analista@amigochas.com"
                )
        );

        // Guardar los usuarios
        usuarioRepository.saveAll(usuarios);

        System.out.println("✅ Usuarios inicializados con éxito:");
        System.out.println("   👤 Usuario: admin / Contraseña: admin123 / Rol: ADMIN");
        System.out.println("   👤 Usuario: operador / Contraseña: operador123 / Rol: OPERATOR");
        System.out.println("   👤 Usuario: analista / Contraseña: analista123 / Rol: ANALYST");
    }
}
