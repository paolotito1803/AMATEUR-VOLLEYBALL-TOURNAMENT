package org.example.amateurvolleyballtournament.ticketing.web;

import org.example.amateurvolleyballtournament.teams.PlayerDTO;
import org.example.amateurvolleyballtournament.teams.StadiumDTO;
import org.example.amateurvolleyballtournament.teams.VolleyballRole;
import org.example.amateurvolleyballtournament.ticketing.TicketDTO;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.amateurvolleyballtournament.ticketing.web.BookTicketRequest;

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
    public List<TicketDTO> getTicketsUser(@RequestParam  String userId) {
        return ticketService.getAllTicketsByUserId(userId);
    }

    @GetMapping("/match/seats")
    public int getAvailableSeats(String match){
        return ticketService.availableSeats(match);
    }

    // Use @RequestBody is more standard, easier for frontend and complies to our use of DTOs
    @PostMapping("/booking")
    public ResponseEntity<String> bookTicket(@RequestBody BookTicketRequest request) {
        ticketService.addTicket(request.numSeat(), request.userId(), request.matchId());

        return ResponseEntity.ok("Ticket booked successfully");
    }

    // DELETE http://localhost:8080/api/teams/reset
    @DeleteMapping("/reset")
    public ResponseEntity<?> resetData() {
        // TODO: UPDATE THIS WITH PROPER AUTHORIZATION LOGIC
        /*if (!usersService.isAdmin("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESS DENIED: Only Admins can reset the database.");
        }*/

        ticketService.deleteAllTickets();
        return ResponseEntity.ok("All tickets have been deleted.");
    }
}
