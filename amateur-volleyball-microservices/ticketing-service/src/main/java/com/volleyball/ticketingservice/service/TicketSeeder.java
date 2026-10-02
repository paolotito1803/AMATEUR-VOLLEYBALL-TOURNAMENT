package com.volleyball.ticketingservice.service;

import com.volleyball.common.dto.MatchDTO;
import com.volleyball.common.dto.TicketDTO;
import com.volleyball.common.dto.TournamentDTO;
import com.volleyball.common.events.SeedingCompleteEvent;
import com.volleyball.ticketingservice.integration.TournamentIntegration;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TicketSeeder {

    private final TicketService ticketService;
    private final TournamentIntegration tournamentIntegration;
    private final RabbitTemplate rabbitTemplate;

    public TicketSeeder(TicketService ticketService, TournamentIntegration tournamentIntegration, RabbitTemplate rabbitTemplate) {
        this.ticketService = ticketService;
        this.tournamentIntegration = tournamentIntegration;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Waits for Tournament to finish seeding
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "seeding.ticketing.queue", durable = "true"),
            exchange = @Exchange(value = "volleyball.internal", type = "topic", ignoreDeclarationExceptions = "true"),
            key = "seeding.tournament.done"
    ))
    public void onTournamentSeeded(SeedingCompleteEvent event) {
        seed();
    }

    public void seed() {
        System.out.println("--- Seeding Tickets ---");

        // Needs to find valid Match IDs from tournament
        List<TournamentDTO> tournaments = tournamentIntegration.getAllTournaments();

        if (tournaments.isEmpty()) {
            System.out.println("No tournaments found. Skipping ticket seeding.");
            sendSeedingEvent();
            return;
        }

        // Book a ticket for "id_viewer_1" on the first match of the first tournament
        String targetUserId = "id_viewer_1";

        // Find a valid match
        TournamentDTO t = tournaments.getFirst();
        if (t.matches() != null && !t.matches().isEmpty()) {
            Optional<MatchDTO> matchOpt = t.matches().stream()
                    .filter(m -> m.matchIndex() == 1)
                    .findFirst();

            if (matchOpt.isPresent()) {
                MatchDTO match = matchOpt.get();
                String matchId = match.matchId();

                List<TicketDTO> userTickets = ticketService.getUserTickets(targetUserId);
                boolean alreadyBooked = userTickets.stream()
                        .anyMatch(ticket -> ticket.match().matchId().equals(matchId));

                if (!alreadyBooked) {
                    try {
                        System.out.println("Booking ticket for Viewer 1 on Match #1: " + match.teamA() + " vs " + match.teamB());
                        ticketService.bookTicket(50, matchId, targetUserId);
                    } catch (Exception e) {
                        System.err.println("Failed to seed ticket: " + e.getMessage());
                    }
                } else {
                    System.out.println("Viewer 1 already has a ticket for this match.");
                }
            }
        }

        System.out.println("--- Tickets Seeded ---");
        sendSeedingEvent();
    }

    private void sendSeedingEvent() {
        SeedingCompleteEvent event = new SeedingCompleteEvent("ticketing-service", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend("volleyball.internal", "seeding.ticketing.done", event);
        System.out.println(">> Signal Sent: Ticketing Ready");
    }
}
