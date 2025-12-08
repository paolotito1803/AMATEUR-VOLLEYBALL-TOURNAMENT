package org.example.amateurvolleyballtournament.tournament;

import java.util.List;

public interface TournamentService {

    /* TODO: capire se specificare unicamente i match per le modifiche o le assegnazioni o se inserire anche
        l'id del torneo per questioni di ricerca */

    // Tournament creation
    List<TournamentDTO> getAllTournaments();

    // Groups creation
    // For implementation: assign a new group list to a tournament
    // void createTournamentGroups(TournamentDTO tournament, List<GroupDTO> groupList);

    // Edit tournament name
    // void editTournamentName(TournamentDTO tournament, String newName);

    // Edit tournament match
    // For implementation: edit a specific group in the group list of a tournament
    // void editTournamentMatch(TournamentDTO tournament, MatchDTO match);

    // Update tournament match
    // void updateTournamentMatch(TournamentDTO tournament, List<MatchDTO> matchUpdateList);

    // Update match date
    // void updateMatchDate(MatchDTO match, Date newDate);

    // Update Team List. Also update matches (linked to the tournament's teams)
    // void updateTournamentTeams(TournamentDTO tournament, List<TeamDTO> newTeamList);

    // Assign referee
    // void assignMatchReferee(MatchDTO match, String referee);

    // Export tournament information
    // For implementation: search for the match by id OR replace parameter with a TournamentDTO
    // void exportTournamentInformation(TournamentDTO tournament);

    // Update results
    // For implementation: list of groups with edited scores, position ecc... to update in tournament's group list
    // void updateTournamentResults(TournamentDTO tournament, List<MatchDTO> editedScores);

    // Assign Stadium
    // void assignMatchStadium(MatchDTO match, String stadium);
}
