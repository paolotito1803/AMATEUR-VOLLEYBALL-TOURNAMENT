package org.example.amateurvolleyballtournament.teams.web;

import org.example.amateurvolleyballtournament.teams.*;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// For Testing
@RestController
@RequestMapping("/api/teams") // This is the address: http://localhost:8080/api/teams
class TeamRestController {

    private final TeamService teamService;
    private final UsersService usersService;

    TeamRestController(TeamService teamService, UsersService usersService) {
        this.teamService = teamService;
        this.usersService = usersService;
    }

    // GET http://localhost:8080/api/teams
    @GetMapping
    public List<TeamDTO> getAllTeams() {
        return teamService.getAllTeams();
    }

    // GET http://localhost:8080/api/teams/{id}
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable String teamId) {
        return teamService.getTeam(teamId)
                .map(ResponseEntity::ok)       // If found, return 200 OK + JSON
                .orElse(ResponseEntity.notFound().build()); // If empty, return 404 Not Found
    }

    // POST http://localhost:8080/api/teams/register
    @PostMapping("/register")
    public ResponseEntity<String> registerTeam(@RequestParam String coachId,
                                               @RequestParam String coachName,
                                               @RequestParam String teamName) {
        try {
            teamService.registerTeam(coachId, coachName, teamName);
            return ResponseEntity.ok("Team registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DEV TOOLS

    // DELETE http://localhost:8080/api/teams/reset
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetData() {
        // TO DO: UPDATE THIS WITH PROPER AUTHORIZATION LOGIC
        if (!usersService.isAdmin("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ACCESS DENIED: Only Admins can reset the database.");
        }

        teamService.deleteAllTeams();
        return ResponseEntity.ok("All teams, players, and stadiums deleted.");
    }

    // POST http://localhost:8080/api/teams/seed
    @PostMapping("/seed")
    public ResponseEntity<String> seedData() {
        // TO DO: UPDATE THIS WITH PROPER AUTHORIZATION LOGIC
        if (!usersService.isAdmin("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ACCESS DENIED: Only Admins can seed data.");
        }

        try {
            // Check if data exists to avoid duplicates/errors
            if (!teamService.getAllTeams().isEmpty()) {
                return ResponseEntity.badRequest().body("Database is not empty. Please call /reset first.");
            }

            // 1. Register Teams
            teamService.registerTeam("coach_mario", "Mario Rossi", "Thunder Spikes");
            teamService.registerTeam("coach_luigi", "Luigi Verdi", "Green Walls");

            // 2. Add Stadiums
            teamService.updateStadium("coach_mario", new StadiumDTO("PalaMushroom", 500));
            teamService.updateStadium("coach_luigi", new StadiumDTO("Luigi's Mansion Arena", 300));

            // 3. Add Players (Mario's Team)
            teamService.addPlayer("coach_mario", new PlayerDTO("Toad", 1, VolleyballRole.LIBERO));
            teamService.addPlayer("coach_mario", new PlayerDTO("Yoshi", 10, VolleyballRole.SETTER));
            teamService.addPlayer("coach_mario", new PlayerDTO("Peach", 7, VolleyballRole.PLAYER));

            // 4. Add Players (Luigi's Team)
            teamService.addPlayer("coach_luigi", new PlayerDTO("Daisy", 4, VolleyballRole.PLAYER));
            teamService.addPlayer("coach_luigi", new PlayerDTO("Waluigi", 99, VolleyballRole.PLAYER));

            return ResponseEntity.ok("Dev Data Seeded Successfully!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error seeding data: " + e.getMessage());
        }
    }
}
