package org.example.amateurvolleyballtournament.teams.internal;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Embeddable: this is not a table, but part of another table
@Embeddable
@Data // Lombok to Generates getters, setters, toString
@NoArgsConstructor
@AllArgsConstructor
class StadiumEmbeddable {
    private String stadiumName;
    private int stadiumCapacity;
}
