package org.example.amateurvolleyballtournament.teams.internal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.amateurvolleyballtournament.teams.VolleyballRole;

@Entity // JPA: This is a table
@Table(name = "players", schema = "teams_module")
@Data // Lombok to Generates getters, setters, toString
@NoArgsConstructor
class PlayerEntity {

    @Id // JPA: Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA: Auto-increment (1, 2, 3...)
    private Long id;

    private String name;
    private int number;

    @Enumerated(EnumType.STRING) // JPA: Save the ENUM as text ("LIBERO"), not a number
    private VolleyballRole role;

    // RELATIONSHIP: Many Players belong to One Team
    @ManyToOne
    @JoinColumn(name = "team_id") // Creates a foreign key column 'team_id'
    private TeamEntity team;

    public PlayerEntity(String name, int number, VolleyballRole role, TeamEntity team) {
        this.name = name;
        this.number = number;
        this.role = role;
        this.team = team;
    }
}
