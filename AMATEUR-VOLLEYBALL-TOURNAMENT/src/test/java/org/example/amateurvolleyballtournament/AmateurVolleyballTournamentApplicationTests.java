package org.example.amateurvolleyballtournament;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class AmateurVolleyballTournamentApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void verifyModularity() {
        // This analyzes the project structure
        // It fails if we have circular dependencies between modules.
        ApplicationModules modules = ApplicationModules.of(AmateurVolleyballTournamentApplication.class);
        modules.verify();

        // his prints the module structure to the console
        modules.forEach(System.out::println);
    }

}
