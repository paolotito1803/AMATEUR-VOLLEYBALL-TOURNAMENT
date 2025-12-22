package org.example.amateurvolleyballtournament.tournament.web;
import org.example.amateurvolleyballtournament.teams.StadiumDTO;
import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments") // http://localhost:8080/api/tournaments
class TournamentRestController {

    private final TournamentService tournamentService;
    private final TeamService teamService;

    TournamentRestController(TournamentService tournamentService, TeamService teamService) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    @GetMapping("/all")
    public List<TournamentDTO> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    //JSON Date (like "2025-12-25T15:00:00")
    @GetMapping("/match/date")
    public LocalDateTime getMatchDate(@RequestParam("matchId") String matchId) {
        return tournamentService.getMatchDate(matchId);
    }

    @GetMapping("/match/referee")
    public String getMatchReferee(@RequestParam("matchId") String matchId) {return tournamentService.getMatchReferee(matchId); }

    @GetMapping("/match/get")
    public MatchDTO getMatchInformation(@RequestParam("matchId") String matchId) {
        return tournamentService.getMatchInformation(matchId).orElseThrow(() -> new RuntimeException("No match found for matchId: " + matchId));
    }

    @GetMapping("/match/get_next_match")
    public MatchDTO getNextMatch(@RequestParam("matchId") String matchId) {
        return tournamentService.getNextMatch(matchId)
                .orElseThrow(() -> new RuntimeException("No match or next match found for matchId: " + matchId));
    }

    @GetMapping("/match/get_match_winner")
    public String getMatchWinner(@RequestParam("matchId") String matchId) {
        return tournamentService.getMatchWinner(matchId);
    }

    @PostMapping("/update_name")
    public ResponseEntity<String> updateTournamentName(@RequestParam String adminId,
                                                       @RequestParam String tournamentId,
                                                       @RequestParam String tournamentName) {

        tournamentService.updateTournamentName(adminId, tournamentId, tournamentName);

        return ResponseEntity.ok("Tournament updated successfully");
    }

    // UpdateTeams
    @PostMapping
    public ResponseEntity<String> updateTeams(@RequestParam String adminId,
                                              @RequestParam List<String> teams,
                                              @RequestParam String tournamentId) {
        tournamentService.updateTeams(adminId, teams, tournamentId);

        return ResponseEntity.ok("Teams updated successfully.");
    }

    @PostMapping("/match/update_referee")
    public ResponseEntity<String> updateMatchReferee(@RequestParam String adminId,
                                                     @RequestParam String matchId,
                                                     @RequestParam String newReferee) {
        tournamentService.updateMatchReferee(adminId, matchId, newReferee);

        return ResponseEntity.ok("Referee " + newReferee + " for match " + matchId + "updated successfully.");
    }

    // Keep Date or switch to String with date format
    // Accepts ISO Date String (like "2025-12-25T15:00:00")
    @PostMapping("/match/update_date")
    public ResponseEntity<String> updateMatchDate(
            @RequestParam String adminId,
            @RequestParam String matchId,
            // Automatically parses string -> LocalDateTime
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newMatchDate) {

        tournamentService.updateMatchDate(adminId, matchId, newMatchDate);

        return ResponseEntity.ok("Match " + matchId + " updated successfully with date " + newMatchDate);
    }

    @PostMapping("/match/update_free_seats")
    public ResponseEntity<String> updateMatchFreeSeats(@RequestParam String adminId,
                                                       @RequestParam String matchId,
                                                       @RequestParam int newFreeSeats) {
        tournamentService.updateMatchFreeSeats(adminId, matchId, newFreeSeats);

        return ResponseEntity.ok("Free seats for match " + matchId + "updated successfully to value " + newFreeSeats);
    }

    @PostMapping("/match/update_statium")
    public ResponseEntity<String> updateMatchStadium(@RequestParam String adminId,
                                                     @RequestParam String matchId,
                                                     @RequestParam String newStadium) {
        tournamentService.updateMatchStadium(adminId, matchId, newStadium);
        // TODO: update seats and date if stadium gets changed
        return ResponseEntity.ok("Stadium " + newStadium + " updated successfully.");
    }

    // Also automatically manage matches creation for first stage
    @PostMapping("/create_tournament")
    public ResponseEntity<String> createTournament(@RequestParam String adminId,
                                                        @RequestParam String tournamentName,
                                                        @RequestParam int maxTournamentTeams,
                                                        @RequestParam List<String> teams) {
        tournamentService.createTournament(adminId, tournamentName, maxTournamentTeams, teams);

        return ResponseEntity.ok("Tournament " + tournamentName + " created successfully.");
    }

    @PostMapping("/match/create_match")
    public ResponseEntity<String> createMatch(@RequestParam String adminId,
                                              @RequestParam String tournamentId,
                                              @RequestParam int stage,
                                              @RequestParam String teamA,
                                              @RequestParam String teamB) {

        tournamentService.createMatch(adminId, tournamentId, stage, teamA, teamB);

        return ResponseEntity.ok("Match for tournament " + tournamentId + " created successfully.");
    }

    // Update match winner and assign it to upper matches (or create a new stage)
    @PostMapping("/match/update_winner")
    public ResponseEntity<String> updateMatchWinner(@RequestParam String adminId,
                                                    @RequestParam String matchId,
                                                    @RequestParam String winner) {
        tournamentService.updateMatchWinner(adminId, matchId, winner);

        return ResponseEntity.ok("Match winner " + winner + " for match " + matchId + "  updated successfully.");
    }
}
