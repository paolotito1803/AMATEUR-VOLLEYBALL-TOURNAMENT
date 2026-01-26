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

        // First check if available seats
        MatchDTO match = tournamentService.getMatchInformation(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        if(numSeat > match.freeSeats()) {
            throw new IllegalArgumentException("Seat number is greater than available seats");
        }

        boolean reserved = tournamentService.reserveSeat(match.matchId()); // this should fix race condition
        if (!reserved) {
            throw new IllegalStateException("No seats available");
        }

        TicketEntity ticket = new TicketEntity(numSeat, matchId, userId);
        db.save(ticket);
    }

    @Override
    public List<TicketDTO> getAllTicketsByUserId(String userId) {
        return db.findTicketsByUserId(userId).stream().map(this::toDTO).toList();
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

    private TicketDTO toDTO(TicketEntity entity) {
        MatchDTO m = tournamentService.getMatchInformation(entity.getMatch())
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        return new TicketDTO(entity.getId(), entity.getNumSeat(), m, entity.getUserId());
    }
}
