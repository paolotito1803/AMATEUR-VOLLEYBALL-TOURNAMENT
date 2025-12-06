package org.example.amateurvolleyballtournament.tournament.internal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tournament", schema = "tournament_module")
@Data // Lombok to Generates getters, setters, toString
@NoArgsConstructor
public class TournamentEntity {
    @Id // JPA: Primary Key
    private String tournamentId; // UUID
    private String tournamentName;

    public TournamentEntity(String tournamentId) {
        this.tournamentId = UUID.randomUUID().toString();
        this.tournamentName = tournamentId;
    }
}
