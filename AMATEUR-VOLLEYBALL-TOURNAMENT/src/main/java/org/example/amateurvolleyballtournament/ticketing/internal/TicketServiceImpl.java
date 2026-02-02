package org.example.amateurvolleyballtournament.ticketing.internal;

import org.example.amateurvolleyballtournament.ticketing.TicketDTO;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UserDTO;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class TicketServiceImpl implements TicketService {
    private TicketPersistence db;
    private TournamentService tournamentService;
    private UsersService usersService;

    TicketServiceImpl(TicketPersistence db,  TournamentService tournamentService, UsersService usersService) {
        this.db = db;
        this.tournamentService = tournamentService;
        this.usersService = usersService;
    }

    @Override
    public void addTicket(int numSeat, String userId, String matchId) {
        if(userId == null || userId.isEmpty() || matchId == null || matchId.isEmpty() || numSeat <= 0)
            throw new IllegalArgumentException("userId, match and numSeat cannot be empty");

        // Check if this specific seat is already taken
        if (db.existsTicket(matchId, numSeat)) {
            throw new IllegalArgumentException("Seat " + numSeat + " is already occupied.");
        }

        // First check if available seats
        MatchDTO match = tournamentService.getMatchInformation(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        // Instead, check if there are ANY seats left
        if (match.freeSeats() <= 0) {
            throw new IllegalStateException("Match is sold out.");
        }

        // Rely on the atomic reservation in TournamentService
        boolean reserved = tournamentService.reserveSeat(match.matchId());
        if (!reserved) {
            throw new IllegalStateException("No seats available (Concurrency error).");
        }

        TicketEntity ticket = new TicketEntity(numSeat, matchId, userId);
        db.save(ticket);
    }

    @Override
    public List<TicketDTO> getAllTicketsByUserId(String userId) {
        return db.findTicketsByUserId(userId).stream()
                .map(this::toDTO)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public int availableSeats(String match) {
        Optional<MatchDTO> m = tournamentService.getMatchInformation(match);
        if(m.isPresent())
            return m.get().freeSeats();
        else
            throw new RuntimeException("No match seats have been found");
    }

    @Override
    public void deleteAllTickets(){
        db.deleteAll();
    }

    private Optional<TicketDTO> toDTO(TicketEntity entity) {
        try {
            Optional<MatchDTO> matchOpt = tournamentService.getMatchInformation(entity.getMatch());

            // If the tournament/match was deleted but the ticket remains, ignore the ticket
            if (matchOpt.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new TicketDTO(entity.getId(), entity.getNumSeat(), matchOpt.get(), entity.getUserId()));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
}
