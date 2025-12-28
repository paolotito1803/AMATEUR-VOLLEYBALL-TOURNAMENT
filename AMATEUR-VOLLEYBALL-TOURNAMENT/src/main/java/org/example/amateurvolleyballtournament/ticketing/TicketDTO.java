package org.example.amateurvolleyballtournament.ticketing;

import java.time.LocalDateTime;

public record TicketDTO(String id, int numSeat, LocalDateTime date, String match, String userId) {
}
