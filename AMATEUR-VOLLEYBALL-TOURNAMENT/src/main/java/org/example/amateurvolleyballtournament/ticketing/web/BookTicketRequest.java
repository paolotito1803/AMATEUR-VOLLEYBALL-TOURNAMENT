package org.example.amateurvolleyballtournament.ticketing.web;

public record BookTicketRequest(int numSeat, String matchId, String userId) {
}
