package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ApplicationModuleTest
@Transactional
class TournamentModuleTest {

    @Autowired
    TournamentService tournamentService;

    @MockitoBean
    UsersService usersService;

    @MockitoBean
    TeamService teamService;

    @Test
    void shouldCreateStandard4TeamTournament() {
        String tName = "Standard Cup " + UUID.randomUUID();
        List<String> teams = List.of("A", "B", "C", "D");

        mockExistingTeams(teams);

        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));

        List<MatchDTO> matches = tournament.matches();
        // 4 teams -> 4 slots -> 3 matches total (2 semis, 1 final)
        assertThat(matches).hasSize(3);

        // Check Round 1 Matches
        MatchDTO match1 = matches.stream().filter(m -> m.matchIndex() == 1).findFirst().orElseThrow();
        MatchDTO match2 = matches.stream().filter(m -> m.matchIndex() == 2).findFirst().orElseThrow();

        assertThat(match1.teamA()).isEqualTo("A");
        assertThat(match1.teamB()).isEqualTo("B");
        assertThat(match2.teamA()).isEqualTo("C");
        assertThat(match2.teamB()).isEqualTo("D");
    }

    @Test
    void shouldHandleByesAutomaticallyFor3Teams() {
        // 3 Teams means 4 Slots. One team gets a BYE
        String tName = "Odd Cup " + UUID.randomUUID();
        List<String> teams = List.of("A", "B", "C");

        mockExistingTeams(teams);

        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        List<MatchDTO> matches = tournament.matches();

        // Check Round 1
        // Expected (A vs B) and (C vs BYE)
        MatchDTO byeMatch = matches.stream()
                .filter(m -> "BYE".equals(m.teamB()) || "BYE".equals(m.teamA()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("BYE match not found"));

        // Check that the winner was already declared by the system
        assertThat(byeMatch.winner()).isNotEqualTo("BYE");
        assertThat(byeMatch.winner()).isIn("A", "B", "C");

        // Check Round 2
        MatchDTO finalMatch = matches.stream().filter(m -> m.stage() == 2).findFirst().orElseThrow();

        // One slot in the final should already be filled by the auto winner
        boolean isSlotAFilled = finalMatch.teamA() != null && !finalMatch.teamA().isEmpty();
        boolean isSlotBFilled = finalMatch.teamB() != null && !finalMatch.teamB().isEmpty();

        // Only one slot should be filled, the other waits for A vs B
        assertThat(isSlotAFilled ^ isSlotBFilled).isTrue();
    }

    @Test
    void shouldHandleDeepRecursionFor9Teams() {
        // 9 Teams -> 16 Slots.
        // Team 9 should land in Round 3
        String tName = "Huge Cup " + UUID.randomUUID();
        List<String> teams = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9");

        mockExistingTeams(teams);

        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));

        // Team 9 should appear in a Round 3 match
        boolean reachedRound3 = tournament.matches().stream()
                .anyMatch(m -> m.stage() == 3 && ("9".equals(m.teamA()) || "9".equals(m.teamB())));

        assertThat(reachedRound3)
                .as("Team 9 should have auto-advanced to Round 3")
                .isTrue();
    }

    @Test
    void shouldAdvanceWinnerToNextMatch() {
        String tName = "Flow Cup " + UUID.randomUUID();
        List<String> teams = List.of("Alpha", "Beta", "Gamma", "Delta");

        mockExistingTeams(teams);

        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match1 = tournament.matches().stream().filter(m -> m.matchIndex() == 1).findFirst().orElseThrow();
        String matchId = match1.matchId();

        tournamentService.updateMatchWinner(matchId, "Alpha");

        TournamentDTO updatedTournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO finalMatch = updatedTournament.matches().stream().filter(m -> m.stage() == 2).findFirst().orElseThrow();

        assertThat(finalMatch.teamA()).isEqualTo("Alpha");
    }

    @Test
    void shouldRejectLessThanTwoTeams() {
        List<String> teams = List.of("Lonely Team");

        assertThatThrownBy(() -> tournamentService.createTournament("Fail Cup", teams))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 2 teams");
    }

    @Test
    void shouldRejectDuplicateTeams() {
        List<String> teams = List.of("Team A", "Team A"); // Duplicate

        assertThatThrownBy(() -> tournamentService.createTournament("Clone Cup", teams))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate teams");
    }

    // Mocks the TeamService to pretend that the requested teams actually exist
    private void mockExistingTeams(List<String> teamNames) {
        List<TeamDTO> dtos = new ArrayList<>();
        for (String name : teamNames) {
            dtos.add(new TeamDTO("id_" + name, "coach_" + name, name, "Coach " + name, List.of(), null));
        }
        when(teamService.getAllTeams()).thenReturn(dtos);
    }
}