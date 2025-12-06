package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.util.Date;

public record MatchDTO(String matchId, String stadium, int freeSeats, Date matchDate, String referee, int position, int stage, String teamA, String teamB, int scoreA, int scoreB, String winner, MatchDTO nextMatch) {
}
