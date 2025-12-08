package org.example.amateurvolleyballtournament.ticketing.internal;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

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

    // READS
    public List<TicketEntity> findTicketsByUserId(String user_id) {
        return ticketRepo.findByUserId(user_id);
    }

    public List<TicketEntity> findTicketByMatchId(String match) {
        return ticketRepo.findByMatch(match);
    }
}
