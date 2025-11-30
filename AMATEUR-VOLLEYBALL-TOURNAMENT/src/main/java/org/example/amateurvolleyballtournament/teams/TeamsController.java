package org.example.amateurvolleyballtournament.teams;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// For Testing
@RestController
@RequestMapping("/api/teams") // This is the address: http://localhost:8080/api/teams
class TeamController {

    private final TeamService teamService;

    TeamController(TeamService teamService) {
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
}
