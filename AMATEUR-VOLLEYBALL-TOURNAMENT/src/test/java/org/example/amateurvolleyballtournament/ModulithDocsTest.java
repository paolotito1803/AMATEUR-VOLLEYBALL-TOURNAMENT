package org.example.amateurvolleyballtournament;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithDocsTest {

    ApplicationModules modules = ApplicationModules.of(AmateurVolleyballTournamentApplication.class);

    @Test
    void writeDocumentationSnippets() {
        modules.verify();

        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
