package org.example.amateurvolleyballtournament.teams.internal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity // JPA: This is a table
@Table(name = "teams", schema = "teams_module")
@Data // Lombok to Generates getters, setters, toString
@NoArgsConstructor
class TeamEntity {

    @Id // JPA: Primary Key
    private String id; // UUID

    private String coachId;
    private String coachName;
    private String name;

    @Embedded // JPA: Take the fields from StadiumEmbeddable and put them in THIS table
    private StadiumEmbeddable stadium;

    // RELATIONSHIP: One Team has Many Players
    // cascade = ALL: If you save the Team, it automatically saves the Players.
    // orphanRemoval = true: If you remove a player from this list, delete them from DB.
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> players = new ArrayList<>();

    public TeamEntity(String coachId, String coachName, String teamName) {
        this.id = UUID.randomUUID().toString();
        this.coachId = coachId;
        this.coachName = coachName;
        this.name = teamName;
    }

    // Helper to keep both sides of the relationship in sync
    public void addPlayer(PlayerEntity player) {
        players.add(player);
        player.setTeam(this);
    }
}
