package com.volleyball.ticketingservice.service;

import com.volleyball.common.dto.MatchDTO;
import com.volleyball.common.dto.TicketDTO;
import com.volleyball.common.events.TicketSoldEvent;
import com.volleyball.ticketingservice.integration.TournamentIntegration;
import com.volleyball.ticketingservice.entities.TicketEntity;
import com.volleyball.ticketingservice.repository.TicketRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository repository;
    private final TournamentIntegration tournamentIntegration;
    private final RabbitTemplate rabbitTemplate;

    public TicketService(TicketRepository repository, 
                         TournamentIntegration tournamentIntegration,
                         RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.tournamentIntegration = tournamentIntegration;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void bookTicket(int numSeat, String matchId, String userId) {
        // Validation
        if (repository.existsByMatchIdAndNumSeat(matchId, numSeat)) {
            throw new IllegalArgumentException("Seat " + numSeat + " is already occupied.");
        }

        // Reserve seat in tournament service
        boolean reserved = tournamentIntegration.reserveSeat(matchId);
        
        if (!reserved) {
            throw new IllegalStateException("No seats available or match not found.");
        }

        // Local create ticket
        TicketEntity ticket = new TicketEntity(numSeat, matchId, userId);
        repository.save(ticket);

        // Send message to rabbitmq
        TicketSoldEvent event = new TicketSoldEvent(ticket.getId(), matchId, userId);
        rabbitTemplate.convertAndSend("ticket.sold.queue", event);
        
        System.out.println("Ticket sold & Event published: " + event);
    }

    public int availableSeats(String matchId){
       Optional<MatchDTO> m = tournamentIntegration.getMatch(matchId);
        if(m.isPresent())
            return m.get().freeSeats();
        else
            throw new RuntimeException("No match seats have been found");
    }

    public List<Integer> getTakenSeats(String matchId) {
        return repository.findTakenSeatsByMatchId(matchId);
    }

    public List<TicketDTO> getUserTickets(String userId) {
        return repository.findByUserId(userId).stream()
                .map(t -> new TicketDTO(t.getId(), t.getNumSeat(),
                        tournamentIntegration.getMatch(t.getMatchId()).orElse(null),
                        t.getUserId())).toList();
    }

    public void deleteAllTickets() {
        repository.deleteAll();
    }

    public void deleteTicketByMatchId(String matchId) {
        repository.deleteByMatchId(matchId);
    }
}
