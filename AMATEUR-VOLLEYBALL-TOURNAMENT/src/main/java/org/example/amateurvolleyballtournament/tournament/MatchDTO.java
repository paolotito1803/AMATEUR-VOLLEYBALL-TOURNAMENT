package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.time.LocalDateTime;

public record MatchDTO(String matchId, String stadium, int freeSeats,
                       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime matchDate,
                       String referee, int stage, String teamA, String teamB, int scoreA, int scoreB, String winner,
                       String nextMatch) {
}
