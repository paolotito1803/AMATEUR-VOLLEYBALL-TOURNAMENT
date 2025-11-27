package org.example.amateurvolleyballtournament.teams.internal;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

// no 'public' before 'class', other modules cannot even see this
@Service
class TeamServiceImpl implements TeamService {

    // Non-working example of interactions
    private final List<TeamDTO> db = new ArrayList<>();

    @Override
    public void registerTeam(TeamDTO team) {
        db.add(team);
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        return db;
    }
}
