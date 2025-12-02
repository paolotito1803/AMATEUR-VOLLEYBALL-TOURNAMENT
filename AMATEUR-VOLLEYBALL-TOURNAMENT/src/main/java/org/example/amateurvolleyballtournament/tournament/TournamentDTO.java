package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.util.Date;
import java.util.List;

public record TournamentDTO(String id, String tournamentName, List<GroupsDTO> groups, List<TeamDTO> teams, String stadium, int freeSeats, Date tournamentDate, String referee) {
}
