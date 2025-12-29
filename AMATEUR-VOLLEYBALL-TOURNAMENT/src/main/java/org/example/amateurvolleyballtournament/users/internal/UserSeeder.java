package org.example.amateurvolleyballtournament.users.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.surname}")
    private String adminSurname;

    @Value("${app.admin.role}")
    private String adminRole;

    @Value("${app.admin.isactive}")
    private boolean adminIsActive;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByEmail(adminEmail).isEmpty()) {
            System.out.println("\uD83C\uDF31 Seeding Admin User...");
            UserEntity admin = new UserEntity(
                    UUID.randomUUID().toString(),
                    adminUsername,
                    adminSurname,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    adminRole,
                    adminIsActive
            );
            userRepository.save(admin);
            System.out.println("✅ Admin created: " + adminEmail);
        }
    }
}
