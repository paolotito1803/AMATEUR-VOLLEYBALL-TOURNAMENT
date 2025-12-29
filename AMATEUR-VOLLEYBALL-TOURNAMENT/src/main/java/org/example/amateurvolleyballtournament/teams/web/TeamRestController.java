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
    public ResponseEntity<String> registerTeam(@RequestBody RegisterTeamRequest request) {
        // If this throws an exception, the GlobalExceptionHandler catches it
        teamService.registerTeam(
                request.coachId(),
                request.coachName(),
                request.teamName()
        );

        return ResponseEntity.ok("Team registered successfully");
    }

    // DEV TOOLS

    // DELETE http://localhost:8080/api/teams/reset
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetData() {
        // TODO: ADD PROPER AUTHORIZATION LOGIC

        teamService.deleteAllTeams();
        return ResponseEntity.ok("All teams, players, and stadiums deleted.");
    }

    // POST http://localhost:8080/api/teams/seed
    @PostMapping("/seed")
    public ResponseEntity<String> seedData() {
        // TODO: ADD PROPER AUTHORIZATION LOGIC

        try {
            // Check if data exists to avoid duplicates/errors
            if (!teamService.getAllTeams().isEmpty()) {
                return ResponseEntity.badRequest().body("Database is not empty. Please call /reset first.");
            }

            // --- TEAM 1: Thunder Spikes (Mario) ---
            teamService.registerTeam("coach_mario", "Mario Rossi", "Thunder Spikes");
            teamService.updateStadium("coach_mario", new StadiumDTO("PalaMushroom", 500));
            teamService.addPlayer("coach_mario", new PlayerDTO("Toad", 1, VolleyballRole.LIBERO));
            teamService.addPlayer("coach_mario", new PlayerDTO("Yoshi", 10, VolleyballRole.SETTER));

            // --- TEAM 2: Green Walls (Luigi) ---
            teamService.registerTeam("coach_luigi", "Luigi Verdi", "Green Walls");
            teamService.updateStadium("coach_luigi", new StadiumDTO("Luigi's Mansion Arena", 300));
            teamService.addPlayer("coach_luigi", new PlayerDTO("Baby Luigi", 4, VolleyballRole.PLAYER));
            teamService.addPlayer("coach_luigi", new PlayerDTO("Polterpup", 99, VolleyballRole.LIBERO));

            // --- TEAM 3: Golden Setters (Wario) ---
            teamService.registerTeam("coach_wario", "Wario Gialli", "Golden Setters");
            teamService.updateStadium("coach_wario", new StadiumDTO("Gold Mine Stadium", 1000));
            teamService.addPlayer("coach_wario", new PlayerDTO("Mona", 11, VolleyballRole.SETTER));
            teamService.addPlayer("coach_wario", new PlayerDTO("Jimmy T", 12, VolleyballRole.PLAYER));

            // --- TEAM 4: Purple Spikers (Waluigi) ---
            teamService.registerTeam("coach_waluigi", "Waluigi Viola", "Purple Spikers");
            teamService.updateStadium("coach_waluigi", new StadiumDTO("Pinball Arena", 400));
            teamService.addPlayer("coach_waluigi", new PlayerDTO("Ashley", 5, VolleyballRole.PLAYER));

            // --- TEAM 5: Pink Princesses (Peach) ---
            teamService.registerTeam("coach_peach", "Peach Princess", "Pink Princesses");
            teamService.updateStadium("coach_peach", new StadiumDTO("Castle Court", 800));
            teamService.addPlayer("coach_peach", new PlayerDTO("Toadette", 22, VolleyballRole.LIBERO));

            // --- TEAM 6: Flower Power (Daisy) ---
            teamService.registerTeam("coach_daisy", "Daisy Princess", "Flower Power");
            teamService.updateStadium("coach_daisy", new StadiumDTO("Sarasaland Gym", 350));
            teamService.addPlayer("coach_daisy", new PlayerDTO("Birdo", 8, VolleyballRole.PLAYER));

            // --- TEAM 7: Kong Smashers (Donkey Kong) ---
            teamService.registerTeam("coach_dk", "Donkey Kong", "Kong Smashers");
            teamService.updateStadium("coach_dk", new StadiumDTO("Jungle Beat Arena", 600));
            teamService.addPlayer("coach_dk", new PlayerDTO("Diddy Kong", 0, VolleyballRole.SETTER));
            teamService.addPlayer("coach_dk", new PlayerDTO("Dixie Kong", 2, VolleyballRole.PLAYER));

            return ResponseEntity.ok("Seeded 7 Teams successfully!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error seeding data: " + e.getMessage());
        }
    }
}
