package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TournamentService {

    List<TournamentDTO> getAllTournaments();

    Optional<TournamentDTO> getTournamentDetails(String tournamentId);

    Optional<TournamentDTO> getTournamentByName(String name);

    void updateTeams(List<String> teams, String tournamentId);

    void updateMatchReferee(String matchId, String newReferee);

    void updateMatchDate(String matchId, LocalDateTime newDate);

    void updateMatchWinner(String matchId, String winner);

    void refUpdateMatchWinner(String matchId, String winner);

    void updateMatchFreeSeats(String matchId, int newFreeSeats);

    void updateMatchStadium(String matchId, String stadium);

    void updateMatchScore(String matchId, int scoreA, int scoreB);

    void updateTournamentName(String tournamentId, String tournamentName);

    List<TeamDTO> getTeams(String tournamentId);

    String getMatchReferee(String matchId);

    LocalDateTime getMatchDate(String matchId);

    Optional<MatchDTO> getMatchInformation(String matchId);

    String getMatchWinner(String matchId);

    Optional<MatchDTO> getNextMatch(String matchId);

    int getMatchCount(String tournamentId);

    int getTeamCount(String tournamentId);

    // Check parameters for creation, fetch matches and teams and manage first stage of tournament
    void createTournament(String tournamentName, List<String> teams);

    // Generate a match: assign team A, team B, stadium, free seats, date ...
    void createMatch(String tournamentId, int stage, String teamA, String teamB);

    void deleteTournament(String tournamentId);

    boolean reserveSeat(String matchId);
}
