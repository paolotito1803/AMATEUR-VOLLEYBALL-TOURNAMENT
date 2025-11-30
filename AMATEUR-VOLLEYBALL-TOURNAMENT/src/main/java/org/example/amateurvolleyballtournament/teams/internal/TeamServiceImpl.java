package org.example.amateurvolleyballtournament.teams.internal;

import org.example.amateurvolleyballtournament.teams.PlayerDTO;
import org.example.amateurvolleyballtournament.teams.StadiumDTO;
import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
class TeamServiceImpl implements TeamService {

    @Override
    public void registerTeam(String coachId, String coachName, String teamName) {

    }

    @Override
    public void changeCoach(String oldCoachId, String newCoachId, String newCoachName) {

    }

    @Override
    public void updateStadium(String coachId, StadiumDTO stadium) {

    }

    @Override
    public void addPlayer(String coachId, PlayerDTO player) {

    }

    @Override
    public void updatePlayer(String coachId, int playerNumber, PlayerDTO updatedDetails) {

    }

    @Override
    public void removePlayer(String coachId, int playerNumber) {

    }

    @Override
    public List<TeamDTO> getAllTeams() {
        return List.of();
    }

    @Override
    public Optional<StadiumDTO> getStadiumByTeamId(String teamId) {
        return Optional.empty();
    }

    @Override
    public StadiumDTO getStadiumByName(String name) {
        return null;
    }

    @Override
    public TeamDTO getTeam(String teamId) {
        return null;
    }
}
