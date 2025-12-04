package org.example.amateurvolleyballtournament.ticketing.web;

import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tickets")
class TicketRestController {

    private final TicketService ticketService;
    private final UsersService usersService;
    private final TournamentService tournamentService;

    public TicketRestController(TicketService ticketService, UsersService usersService,  TournamentService tournamentService) {
        this.ticketService = ticketService;
        this.usersService = usersService;
        this.tournamentService = tournamentService;
    }
}
