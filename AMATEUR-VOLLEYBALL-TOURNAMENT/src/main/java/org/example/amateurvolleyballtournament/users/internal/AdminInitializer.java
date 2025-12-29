/*package org.example.amateurvolleyballtournament.users.internal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        //check if user admin exist
        if(userRepository.findByEmail("admin@volleyball.com").isEmpty()){
            System.out.println("⚠️ Admin not found. Default admin creation...");

            UserEntity admin = new UserEntity(
                    UUID.randomUUID().toString(),
                    "Admin",
                    "Admin",
                    "admin@volleyball.com",
                    passwordEncoder.encode("admin123"), //password,
                    "ADMIN",
                    true
            );
            userRepository.save(admin);
            System.out.println("✅ creation ended successfully: admin@volleyball.com / admin123");
        }
    }
}*/