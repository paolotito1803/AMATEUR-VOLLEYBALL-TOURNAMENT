package org.example.amateurvolleyballtournament.ticketing;

import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public interface TicketService {
    void addTicket(String userId, String matchId);

    List<TicketDTO> getAllTicketsByUserId(String userId);

    int availableSeats();
}
