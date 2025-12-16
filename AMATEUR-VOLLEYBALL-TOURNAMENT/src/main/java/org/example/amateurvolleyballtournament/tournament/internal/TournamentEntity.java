package org.example.amateurvolleyballtournament.tournament.internal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.amateurvolleyballtournament.teams.TeamDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tournament", schema = "tournament_module")
@Data // Lombok to Generates getters, setters, toString
@NoArgsConstructor
public class TournamentEntity {
    // TODO: add a stage parameter that can be update for each stage of the tournament
    @Id // JPA: Primary Key
    private String tournamentId; // UUID
    private String tournamentName;
    private int maxTournamentTeams;
    private List<String> teams;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchEntity> matches  = new ArrayList<>();

    public TournamentEntity(String tournamentName, int maxTournamentTeams, List<String> teams) {
        this.tournamentId = UUID.randomUUID().toString();
        this.tournamentName =  tournamentName;
        this.teams = teams;
        this.maxTournamentTeams = maxTournamentTeams;
    }
}
