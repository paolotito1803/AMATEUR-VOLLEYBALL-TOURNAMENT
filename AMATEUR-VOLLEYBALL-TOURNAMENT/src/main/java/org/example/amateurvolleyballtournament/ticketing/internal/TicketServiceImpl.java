package org.example.amateurvolleyballtournament.ticketing.internal;



import org.example.amateurvolleyballtournament.ticketing.TicketDTO;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
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
        if(userId.isEmpty() || matchId.isEmpty() || numSeat <= 0)
            throw new RuntimeException("userId, match and numSeat cannot be empty");

        // TODO: ISSUE #3, Race Condition here
        // First check if available seats
        MatchDTO match = tournamentService.getMatchInformation(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        if (match.freeSeats() <= 0) {
            throw new IllegalStateException("No seats available");
        }

        TicketEntity ticket = new TicketEntity(numSeat, matchId, userId);
        db.save(ticket);

        tournamentService.updateMatchFreeSeats(userId, matchId, match.freeSeats() - 1);
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
        return new TicketDTO(entity.getId(), entity.getNumSeat(), entity.getMatch(), entity.getUserId());
    }
}
