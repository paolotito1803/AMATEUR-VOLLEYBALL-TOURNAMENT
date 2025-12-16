package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.util.Date;

public record MatchDTO(String matchId, String stadium, int freeSeats, String matchDate, String referee, int stage, String teamA, String teamB, int scoreA, int scoreB, String winner, String nextMatch) {
}
