package org.example.amateurvolleyballtournament.ticketing.internal;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class TicketPersistance {
    private TicketRepository ticketRepo;

    TicketPersistance(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    @Transactional
    public void save(TicketEntity ticket) {
        ticketRepo.save(ticket);
    }
}
