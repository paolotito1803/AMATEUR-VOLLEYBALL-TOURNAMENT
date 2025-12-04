package org.example.amateurvolleyballtournament.ticketing.internal;

import org.example.amateurvolleyballtournament.ticketing.TicketDTO;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
class TicketServiceImpl implements TicketService {
    @Override
    public void addTicket(String userId, String matchId) {

    }

    @Override
    public List<TicketDTO> getAllTicketsByUserId(String userId) {
        return List.of();
    }

    @Override
    public int availableSeats() {
        return 0;
    }
}
