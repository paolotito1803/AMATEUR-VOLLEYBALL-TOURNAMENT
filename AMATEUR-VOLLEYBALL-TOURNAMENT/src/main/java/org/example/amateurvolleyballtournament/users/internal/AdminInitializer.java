package org.example.amateurvolleyballtournament.users.internal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Controlliamo se esiste già un utente con email "admin@volleyball.com"
        if (userRepository.findByEmail("admin@volleyball.com").isEmpty()) {

            System.out.println("⚠️ Nessun Admin trovato. Creazione utente Admin di default...");

            UserEntity admin = new UserEntity(
                    UUID.randomUUID().toString(),
                    "Super",
                    "Admin",
                    "admin@volleyball.com",
                    passwordEncoder.encode("admin123"), // Password: admin123
                    "ADMIN",
                    true  // Attivo
            );

            userRepository.save(admin);
            System.out.println("✅ Admin creato: admin@volleyball.com / admin123");
        }
    }
}