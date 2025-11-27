package org.example.amateurvolleyballtournament.teams;

import java.util.List;

// Important: The Service is the only thing other modules can see.
public interface TeamService {
    // Example methods
    void registerTeam(TeamDTO team);
    List<TeamDTO> getAllTeams();
}