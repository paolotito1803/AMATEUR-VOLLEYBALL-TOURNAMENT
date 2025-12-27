package org.example.amateurvolleyballtournament.teams;

import org.example.amateurvolleyballtournament.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    void shouldAddPlayerToTeam() {
        // 1. Setup Team
        String coachId = "coach_" + UUID.randomUUID();
        createTeamForCoach(coachId);

        // 2. Add Player
        PlayerDTO newPlayer = new PlayerDTO("Michael Jordan", 23, VolleyballRole.PLAYER);
        teamService.addPlayer(coachId, newPlayer);

        // 3. Verify
        TeamDTO team = getTeamByCoach(coachId);
        assertThat(team.players()).hasSize(1);
        assertThat(team.players().getFirst().name()).isEqualTo("Michael Jordan");
    }

    @Test
    void shouldRejectRegistrationIfUserIsNotCoach() {
        String randomUserId = "user_" + UUID.randomUUID();

        // Mock: The user service says "False, this guy is not a coach"
        when(usersService.isCoach(randomUserId)).thenReturn(false);

        // Assert that calling the method throws the expected exception
        assertThatThrownBy(() ->
                teamService.registerTeam(randomUserId, "Imposter", "Fake Team")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    void shouldRejectDuplicateTeamNames() {
        String coach1 = "coach_A_" + UUID.randomUUID();
        String coach2 = "coach_B_" + UUID.randomUUID();
        String sharedTeamName = "Unique Team " + UUID.randomUUID();

        // Both are valid coaches
        when(usersService.isCoach(anyString())).thenReturn(true);

        // Coach 1 takes the name
        teamService.registerTeam(coach1, "Coach A", sharedTeamName);

        // Coach 2 tries to take the same name -> Should Fail
        assertThatThrownBy(() ->
                teamService.registerTeam(coach2, "Coach B", sharedTeamName)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");
    }

    @Test
    void shouldRejectDuplicatePlayerNumbers() {
        // 1. Setup Team
        String coachId = "coach_" + UUID.randomUUID();
        createTeamForCoach(coachId);

        // 2. Add Player #10
        teamService.addPlayer(coachId, new PlayerDTO("Player One", 10, VolleyballRole.SETTER));

        // 3. Try to add another Player #10 -> Should Fail
        assertThatThrownBy(() ->
                teamService.addPlayer(coachId, new PlayerDTO("Player Two", 10, VolleyballRole.LIBERO))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("number 10 is already taken");
    }

    // HELPER METHODS

    private void createTeamForCoach(String coachId) {
        when(usersService.isCoach(coachId)).thenReturn(true);
        teamService.registerTeam(coachId, "Test Coach", "Team " + coachId);
    }

    private TeamDTO getTeamByCoach(String coachId) {
        return teamService.getAllTeams().stream()
                .filter(t -> t.coachId().equals(coachId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Team not found for test"));
    }
}

