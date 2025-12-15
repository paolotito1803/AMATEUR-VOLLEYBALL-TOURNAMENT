package org.example.amateurvolleyballtournament.tournament.internal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.internal.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity // JPA: This is a table
@Table(name = "match", schema = "tournament_module")
@Data // Lombok to Generates getters, setters, toString
@NoArgsConstructor
public class MatchEntity {
    @Id // JPA: Primary Key
    private String matchId; // UUID
    private String tournament;
    private String stadium;
    private int freeSeats;
    private String matchDate;
    private String referee;
    private int stage;
    private String teamA;
    private String teamB;
    private int scoreA;
    private int scoreB;
    private String winner;
    private String nextMatchId;

    public MatchEntity(String tournament, String stadium, int freeSeats, String referee, String matchDate, int stage,
                       String teamA, String teamB, int scoreA, int scoreB, String winner, String nextMatchId) {
        this.matchId = UUID.randomUUID().toString();
        this.tournament = tournament;
        this.stadium = stadium;
        this.freeSeats = freeSeats;
        this.matchDate  = matchDate;
        this.referee = referee;
        this.stage = stage;
        this.teamA = teamA;
        this.teamB = teamB;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.winner = winner;
        this.nextMatchId = nextMatchId;
    }
}
