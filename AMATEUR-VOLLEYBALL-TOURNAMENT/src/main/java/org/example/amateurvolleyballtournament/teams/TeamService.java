package org.example.amateurvolleyballtournament.teams;

import java.util.List;
import java.util.Optional;

/**
 * Public Interface for the Teams module.
 * This interface handles two distinct flows:
 * 1. The Coach's Management Flow (using coachId to own/modify the team).
 * 2. The System Integration Flow (Tournament/Ticketing modules reading team data).
 */
public interface TeamService {
    // COACH MANAGEMENT
    // Identifying by coachId is convenient as only a logged privileged coach can modify his own team (or the admin)
    void registerTeam(String coachId, String coachName, String teamName);
    void changeCoach(String oldCoachId, String newCoachId, String newCoachName);
    void updateStadium(String coachId, StadiumDTO stadium);
    void addPlayer(String coachId, PlayerDTO player);
    void updatePlayer(String coachId, int currentNumber, PlayerDTO updatedDetails);
    void removePlayer(String coachId, int playerNumber);

    // SYSTEM QUERIES FOR OTHER MODULES
    List<TeamDTO> getAllTeams();
    Optional<StadiumDTO> getStadiumByTeamId(String teamId);
    Optional<StadiumDTO> getStadiumByName(String name);
    Optional<TeamDTO> getTeam(String teamId);

    // DEV TOOLS
    void deleteAllTeams();
}