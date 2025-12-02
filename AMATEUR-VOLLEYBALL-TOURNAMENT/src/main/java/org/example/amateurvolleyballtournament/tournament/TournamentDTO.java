package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.util.List;

public record TournamentDTO(String id, String tournamentName, List<MatchDTO> groups, List<TeamDTO> teams) {
}
