package org.example.amateurvolleyballtournament.tournament;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.util.Date;
import java.time.LocalDateTime;

public record MatchDTO(String matchId, String stadium, int freeSeats,
                       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                       @JsonSerialize(using = LocalDateTimeSerializer.class) LocalDateTime matchDate,
                       String referee, int stage, int matchIndex, String teamA, String teamB, int scoreA, int scoreB,
                       String winner, String nextMatch) {
}
