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
        // Primero eliminar usuarios simples sin rol
        usuarioRepository.findAll().stream()
            .filter(user -> user.getRole() == null || user.getRole().isEmpty())
            .forEach(user -> usuarioRepository.delete(user));
            
        // Siempre inicializar los usuarios con roles
        initializeUsers();
        
        System.out.println("👤 Usuarios disponibles actualizados.");
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
