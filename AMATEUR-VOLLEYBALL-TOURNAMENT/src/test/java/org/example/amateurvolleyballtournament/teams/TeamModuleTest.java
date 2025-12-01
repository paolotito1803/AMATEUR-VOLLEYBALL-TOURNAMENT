package org.example.amateurvolleyballtournament.teams;

import org.example.amateurvolleyballtournament.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// This annotation automatically isolates the 'teams' module
@ApplicationModuleTest
class TeamModuleTest {

    @Autowired
    TeamService teamService;

    // Mocks the UsersService because it belongs to another module
    @MockitoBean
    UsersService usersService;

    @Test
    void shouldRegisterTeamSuccessfully() {
        // Generate unique IDs so we don't clash with existing data
        String uniqueCoachId = "test_coach_" + UUID.randomUUID();
        String uniqueTeamName = "Test Team " + UUID.randomUUID();

        // Mock the user check for this specific dynamic ID
        when(usersService.isCoach(uniqueCoachId)).thenReturn(true);

        teamService.registerTeam(uniqueCoachId, "Test Coach Name", uniqueTeamName);

        TeamDTO myTeam = teamService.getAllTeams().stream()
                .filter(t -> t.coachId().equals(uniqueCoachId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("The team was not found in the database!"));

        assertThat(myTeam.name()).isEqualTo(uniqueTeamName);
        assertThat(myTeam.coachName()).isEqualTo("Test Coach Name");
    }
}

