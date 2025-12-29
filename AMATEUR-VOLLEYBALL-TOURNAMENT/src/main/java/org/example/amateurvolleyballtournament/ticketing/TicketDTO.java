package org.example.amateurvolleyballtournament.ticketing;

import org.example.amateurvolleyballtournament.tournament.MatchDTO;

public record TicketDTO(String id, int numSeat, MatchDTO match, String userId) {
}
