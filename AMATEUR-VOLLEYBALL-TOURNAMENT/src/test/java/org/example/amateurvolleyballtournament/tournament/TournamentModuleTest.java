package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Test
    void shouldRejectNonExistentTeam() {
        String tName = "Invalid Cup " + UUID.randomUUID();
        List<String> teams = List.of("RealTeam", "FakeTeam");

        // Mock only one team as existing
        mockExistingTeams(List.of("RealTeam"));

        assertThatThrownBy(() -> tournamentService.createTournament(tName, teams))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldUpdateTournamentName() {
        String originalName = "Original Cup " + UUID.randomUUID();
        String newName = "Updated Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamX", "TeamY");

        mockExistingTeams(teams);
        tournamentService.createTournament(originalName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(originalName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        String tournamentId = tournament.id();

        tournamentService.updateTournamentName(tournamentId, newName);

        Optional<TournamentDTO> updatedTournament = tournamentService.getTournamentByName(newName);
        assertThat(updatedTournament).isPresent();
        assertThat(updatedTournament.get().tournamentName()).isEqualTo(newName);
    }

    @Test
    void shouldRejectUpdatingNonExistentTournament() {
        String fakeId = "nonexistent_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.updateTournamentName(fakeId, "New Name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldUpdateMatchReferee() {
        String tName = "Referee Cup " + UUID.randomUUID();
        List<String> teams = List.of("Team1", "Team2");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        tournamentService.updateMatchReferee(matchId, "John Referee");

        String referee = tournamentService.getMatchReferee(matchId);
        assertThat(referee).isEqualTo("John Referee");
    }

    @Test
    void shouldUpdateMatchDate() {
        String tName = "Date Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamA", "TeamB");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        LocalDateTime newDate = LocalDateTime.of(2025, 6, 15, 18, 30);
        tournamentService.updateMatchDate(matchId, newDate);

        LocalDateTime retrievedDate = tournamentService.getMatchDate(matchId);
        assertThat(retrievedDate).isEqualTo(newDate);
    }

    @Test
    void shouldUpdateMatchStadium() {
        String tName = "Stadium Cup " + UUID.randomUUID();
        List<String> teams = List.of("Home", "Away");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        tournamentService.updateMatchStadium(matchId, "Arena Belluno");

        MatchDTO updatedMatch = tournamentService.getMatchInformation(matchId)
                .orElseThrow(() -> new AssertionError("Match not found"));
        assertThat(updatedMatch.stadium()).isEqualTo("Arena Belluno");
    }

    @Test
    void shouldUpdateMatchFreeSeats() {
        String tName = "Seats Cup " + UUID.randomUUID();
        List<String> teams = List.of("Blue", "Red");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        tournamentService.updateMatchFreeSeats(matchId, 150);

        MatchDTO updatedMatch = tournamentService.getMatchInformation(matchId)
                .orElseThrow(() -> new AssertionError("Match not found"));
        assertThat(updatedMatch.freeSeats()).isEqualTo(150);
    }

    @Test
    void shouldReserveSeatSuccessfully() {
        String tName = "Booking Cup " + UUID.randomUUID();
        List<String> teams = List.of("Team1", "Team2");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        // Set initial seats
        tournamentService.updateMatchFreeSeats(matchId, 10);

        boolean reserved = tournamentService.reserveSeat(matchId);
        assertThat(reserved).isTrue();

        MatchDTO updatedMatch = tournamentService.getMatchInformation(matchId)
                .orElseThrow(() -> new AssertionError("Match not found"));
        assertThat(updatedMatch.freeSeats()).isEqualTo(9);
    }

    @Test
    void shouldRejectUpdatingMatchForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.updateMatchReferee(fakeMatchId, "John Doe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectUpdatingDateForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        assertThatThrownBy(() -> tournamentService.updateMatchDate(fakeMatchId, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectUpdatingStadiumForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.updateMatchStadium(fakeMatchId, "Stadium"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectUpdatingSeatsForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.updateMatchFreeSeats(fakeMatchId, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectGettingRefereeForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.getMatchReferee(fakeMatchId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectGettingDateForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.getMatchDate(fakeMatchId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectGettingWinnerForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.getMatchWinner(fakeMatchId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldRejectGettingNextMatchForNonExistentMatch() {
        String fakeMatchId = "match_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.getNextMatch(fakeMatchId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldCreateMatchManually() {
        String tName = "Manual Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamA", "TeamB");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        String tournamentId = tournament.id();

        // Create an additional match manually
        tournamentService.createMatch(tournamentId, 1, "TeamA", "TeamB");

        // Verify the tournament still works
        TournamentDTO updatedTournament = tournamentService.getTournamentDetails(tournamentId)
                .orElseThrow(() -> new AssertionError("Tournament not found"));

        assertThat(updatedTournament.matches()).isNotEmpty();
    }

    @Test
    void shouldRejectWinnerWhenMatchAlreadyHasWinner() {
        String tName = "Duplicate Winner Cup " + UUID.randomUUID();
        List<String> teams = List.of("Winner", "Loser");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        // Set winner first time
        tournamentService.updateMatchWinner(matchId, "Winner");

        // Try to set winner again
        assertThatThrownBy(() -> tournamentService.updateMatchWinner(matchId, "Loser"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already has a winner");
    }

    @Test
    void shouldGetMatchWinner() {
        String tName = "Winner Cup " + UUID.randomUUID();
        List<String> teams = List.of("Victor", "Loser");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        tournamentService.updateMatchWinner(matchId, "Victor");

        String winner = tournamentService.getMatchWinner(matchId);
        assertThat(winner).isEqualTo("Victor");
    }

    @Test
    void shouldRejectInvalidWinner() {
        String tName = "Invalid Winner Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamA", "TeamB");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        assertThatThrownBy(() -> tournamentService.updateMatchWinner(matchId, "TeamC"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("did not play");
    }

    @Test
    void shouldRejectDeclareByeAsWinner() {
        String tName = "BYE Winner Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamA", "TeamB");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO match = tournament.matches().get(0);
        String matchId = match.matchId();

        assertThatThrownBy(() -> tournamentService.updateMatchWinner(matchId, "BYE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot declare BYE");
    }

    @Test
    void shouldGetNextMatch() {
        String tName = "Next Match Cup " + UUID.randomUUID();
        List<String> teams = List.of("A", "B", "C", "D");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        MatchDTO semifinal = tournament.matches().stream()
                .filter(m -> m.matchIndex() == 1)
                .findFirst()
                .orElseThrow();

        Optional<MatchDTO> nextMatch = tournamentService.getNextMatch(semifinal.matchId());

        assertThat(nextMatch).isPresent();
        assertThat(nextMatch.get().stage()).isEqualTo(2);
    }

    @Test
    void shouldDeleteTournament() {
        String tName = "Delete Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamX", "TeamY");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        String tournamentId = tournament.id();

        tournamentService.deleteTournament(tournamentId);

        Optional<TournamentDTO> deleted = tournamentService.getTournamentDetails(tournamentId);
        assertThat(deleted).isEmpty();
    }

    @Test
    void shouldRejectDeletingNonExistentTournament() {
        String fakeId = "nonexistent_" + UUID.randomUUID();

        assertThatThrownBy(() -> tournamentService.deleteTournament(fakeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void shouldGetAllTournaments() {
        String tName1 = "Tournament A " + UUID.randomUUID();
        String tName2 = "Tournament B " + UUID.randomUUID();
        List<String> teams = List.of("T1", "T2");

        mockExistingTeams(teams);

        tournamentService.createTournament(tName1, teams);
        tournamentService.createTournament(tName2, teams);

        List<TournamentDTO> allTournaments = tournamentService.getAllTournaments();

        assertThat(allTournaments)
                .extracting(TournamentDTO::tournamentName)
                .contains(tName1, tName2);
    }

    @Test
    void shouldGetTournamentDetails() {
        String tName = "Details Cup " + UUID.randomUUID();
        List<String> teams = List.of("TeamA", "TeamB");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));

        Optional<TournamentDTO> details = tournamentService.getTournamentDetails(tournament.id());

        assertThat(details).isPresent();
        assertThat(details.get().tournamentName()).isEqualTo(tName);
        assertThat(details.get().matches()).isNotEmpty();
    }

    @Test
    void shouldGetMatchInformation() {
        String tName = "Info Cup " + UUID.randomUUID();
        List<String> teams = List.of("InfoA", "InfoB");

        mockExistingTeams(teams);
        tournamentService.createTournament(tName, teams);

        TournamentDTO tournament = tournamentService.getTournamentByName(tName)
                .orElseThrow(() -> new AssertionError("Tournament not found"));
        String matchId = tournament.matches().get(0).matchId();

        Optional<MatchDTO> matchInfo = tournamentService.getMatchInformation(matchId);

        assertThat(matchInfo).isPresent();
        assertThat(matchInfo.get().matchId()).isEqualTo(matchId);
    }

    // HELPER METHODS

    // Mocks the TeamService to pretend that the requested teams actually exist
    private void mockExistingTeams(List<String> teamNames) {
        List<TeamDTO> dtos = new ArrayList<>();
        for (String name : teamNames) {
            dtos.add(new TeamDTO("id_" + name, "coach_" + name, name, "Coach " + name, List.of(), null));
        }
        when(teamService.getAllTeams()).thenReturn(dtos);
    }
}