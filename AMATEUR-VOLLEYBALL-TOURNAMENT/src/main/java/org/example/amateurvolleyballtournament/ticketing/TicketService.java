package org.example.amateurvolleyballtournament.ticketing;


import java.util.List;

public interface TicketService {
    void addTicket(int numSeat, String userId, String matchId);

    List<TicketDTO> getAllTicketsByUserId(String userId);

    int availableSeats(String match);

    void deleteAllTickets();
}
