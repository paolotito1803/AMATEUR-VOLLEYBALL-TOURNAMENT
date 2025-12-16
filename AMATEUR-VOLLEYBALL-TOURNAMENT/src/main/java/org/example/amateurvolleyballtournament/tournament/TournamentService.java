package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.util.List;
import java.util.Optional;

public interface TournamentService {

    /* TODO: capire se specificare unicamente i match per le modifiche o le assegnazioni o se inserire anche
        l'id del torneo per questioni di ricerca */

    // Tournament creation
    List<TournamentDTO> getAllTournaments();

    void updateTeams(String adminId, List<String> teams, String tournamentId);

    void updateMatchReferee(String adminId, String matchId, String newReferee);

    void updateMatchDate(String adminId, String matchId, String newDate);

    /*
    * Update match winner, search for possible matches in the next stage (stage + 1 and a match with no teamA or teamB)
    * If it does not exist, fetch possible number of matches for the stage (maxTeamSize / 2^stage+1).
    * If there is place for a match, create the match
    */
    void updateTournamentWinner(String adminId, String matchId, String winner);

    void updateMatchFreeSeats(String adminId, String matchId, int newFreeSeats);

    List<TeamDTO> getTeams();

    String getMatchReferee(String matchId);

    String getMatchDate(String matchId);

    Optional<MatchDTO> getMatchInformation(String matchId);

    String getMatchWinner(String matchId);

    Optional<MatchDTO> getNextMatch(String matchId);

    // Check parameters for creation, fetch matches and teams and manage first stage of tournament
    void createTournament(String adminId, String tournamentName, int maxTournamentTeams, List<String> teams);

    // Generate a match: assign team A, team B, stadium, free seats, date ...
    void createMatch(String adminId, String torunamentId, int stage, String teamA, String teamB);

    // Edit tournament name
    // void editTournamentName(TournamentDTO tournament, String newName);

    // Edit tournament match
    // For implementation: edit a specific group in the group list of a tournament
    // void editTournamentMatch(TournamentDTO tournament, MatchDTO match);

    // Export tournament information
    // For implementation: search for the match by id OR replace parameter with a TournamentDTO
    // void exportTournamentInformation(TournamentDTO tournament);
}
