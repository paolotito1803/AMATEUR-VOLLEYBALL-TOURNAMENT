package org.example.amateurvolleyballtournament.teams.web;

import org.example.amateurvolleyballtournament.teams.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams") // This is the address: http://localhost:8080/api/teams
class TeamRestController {
    private final TeamService teamService;

    TeamRestController(TeamService teamService) {
        this.teamService = teamService;
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
    public ResponseEntity<String> registerTeam(@RequestBody TeamRequests.RegisterTeamRequest request) {
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

    @GetMapping("/my-team")
    public ResponseEntity<TeamDTO> getTeamByCoachId(@RequestParam String coachId) {
        return teamService.getAllTeams().stream()
                .filter(t -> t.coachId().equals(coachId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{coachId}/players")
    public ResponseEntity<String> addPlayer(@PathVariable String coachId, @RequestBody PlayerDTO player) {
        teamService.addPlayer(coachId, player);
        return ResponseEntity.ok("Player added");
    }

    @PostMapping("/update_stadium")
    public ResponseEntity<String> updateStadium(@RequestBody TeamRequests.UpdateStadiumRequest request) {
        try {
            teamService.updateStadium(request.coachId(), request.stadium());
            return ResponseEntity.ok("Stadium updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating stadium: " + e.getMessage());
        }
    }

    @PutMapping("/{coachId}/players/{playerNumber}")
    public ResponseEntity<String> updatePlayer(@PathVariable String coachId,
                                               @PathVariable int playerNumber,
                                               @RequestBody PlayerDTO player) {
        teamService.updatePlayer(coachId, playerNumber, player);
        return ResponseEntity.ok("Player updated successfully");
    }

    @DeleteMapping("/{coachId}/players/{playerNumber}")
    public ResponseEntity<String> removePlayer(@PathVariable String coachId,
                                               @PathVariable int playerNumber) {
        teamService.removePlayer(coachId, playerNumber);
        return ResponseEntity.ok("Player removed successfully");
    }
}
