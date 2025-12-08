package org.example.amateurvolleyballtournament.ticketing.web;

import org.example.amateurvolleyballtournament.ticketing.TicketDTO;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping
    public List<TicketDTO> getTicketsUser(String user_id) {
        return ticketService.getAllTicketsByUserId(user_id);
    }

    @GetMapping("/match/seats")
    public int getAvailableSeats(String match){
        return ticketService.availableSeats(match);
    }

    @PostMapping("/booking")
    public ResponseEntity<String> bookTicket(@RequestParam int numSeat, @RequestParam String match, @RequestParam String user){
        ticketService.addTicket(numSeat, match, user);
        return  ResponseEntity.ok("Ticket book successfully");
    }
}
