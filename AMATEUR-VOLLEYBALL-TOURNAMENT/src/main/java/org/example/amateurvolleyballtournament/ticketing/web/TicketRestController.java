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

    // POST http://localhost:8080/api/tickets/seed
    @PostMapping("/seed")
    public ResponseEntity<?> seedData() {
        // TO DO: UPDATE THIS WITH PROPER AUTHORIZATION LOGIC

        try {
            // Check if data exists to avoid duplicates/errors
            if (!ticketService.getAllTicketsByUserId("1").isEmpty()) {
                return ResponseEntity.badRequest().body("Database is not empty. Please call /reset first.");
            }

            // 1. Register Teams
            ticketService.addTicket(1, "1", "1");
            ticketService.addTicket(2, "1", "2");

            return ResponseEntity.ok("Dev Data Seeded Successfully!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error seeding data: " + e.getMessage());
        }
    }

    // DELETE http://localhost:8080/api/teams/reset
    @DeleteMapping("/reset")
    public ResponseEntity<?> resetData() {
        // TO DO: UPDATE THIS WITH PROPER AUTHORIZATION LOGIC
        /*if (!usersService.isAdmin("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ACCESS DENIED: Only Admins can reset the database.");
        }*/

        ticketService.deleteAllTickets();
        return ResponseEntity.ok("All teams, players, and stadiums deleted.");
    }
}
