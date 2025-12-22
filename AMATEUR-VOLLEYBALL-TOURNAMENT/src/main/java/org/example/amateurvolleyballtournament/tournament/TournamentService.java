package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TournamentService {

    // Tournament creation
    List<TournamentDTO> getAllTournaments();

    void updateTeams(String adminId, List<String> teams, String tournamentId);

    void updateMatchReferee(String adminId, String matchId, String newReferee);

    void updateMatchDate(String adminId, String matchId, LocalDateTime newDate);

    void updateMatchWinner(String adminId, String matchId, String winner);

    void updateMatchFreeSeats(String adminId, String matchId, int newFreeSeats);

    void updateMatchStadium(String adminId, String matchId, String stadium);

    void updateTournamentName(String adminId, String tournamentId, String tournamentName);

    List<TeamDTO> getTeams();

    String getMatchReferee(String matchId);

    LocalDateTime getMatchDate(String matchId);

    Optional<MatchDTO> getMatchInformation(String matchId);

    String getMatchWinner(String matchId);

    Optional<MatchDTO> getNextMatch(String matchId);

    // Check parameters for creation, fetch matches and teams and manage first stage of tournament
    void createTournament(String adminId, String tournamentName, int maxTournamentTeams, List<String> teams);

    // Generate a match: assign team A, team B, stadium, free seats, date ...
    void createMatch(String adminId, String torunamentId, int stage, String teamA, String teamB);
}
