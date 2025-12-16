package org.example.amateurvolleyballtournament.ticketing.internal;



import org.example.amateurvolleyballtournament.ticketing.TicketDTO;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        TicketEntity ticket = new TicketEntity(numSeat, matchId, userId);
        db.save(ticket);
    }

    @Override
    public List<TicketDTO> getAllTicketsByUserId(String userId) {
        return db.findTicketsByUserId(userId).stream().map(this::toDTO).toList();
    }

    @Override
    public int availableSeats(String match) {
        return db.findTicketByMatchId(match).size();
    }

    @Override
    public void deleteAllTickets(){
        db.deleteAll();
    }

    private TicketDTO toDTO(TicketEntity entity) {
        return new TicketDTO(entity.getId(), entity.getNumSeat(), entity.getMatch(), entity.getUserId());
    }
}
