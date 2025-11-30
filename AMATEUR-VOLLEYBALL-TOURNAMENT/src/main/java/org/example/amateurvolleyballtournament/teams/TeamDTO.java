package org.example.amateurvolleyballtournament.teams;

import java.util.List;

// DTO pattern, just an example
public record TeamDTO(String id, String coachId, String name, String coachName, List<PlayerDTO> players, StadiumDTO stadium) {
}
