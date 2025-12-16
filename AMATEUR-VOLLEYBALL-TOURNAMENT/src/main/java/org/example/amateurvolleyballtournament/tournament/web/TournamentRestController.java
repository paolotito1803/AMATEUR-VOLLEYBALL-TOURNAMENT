package org.example.amateurvolleyballtournament.tournament.web;
import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/match/date")
    public String getMatchDate(@RequestParam("matchId") String matchId) { return tournamentService.getMatchDate(matchId); }

    @GetMapping("/match/referee")
    public String getMatchReferee(@RequestParam("matchId") String matchId) {return tournamentService.getMatchReferee(matchId); }

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
    @PostMapping("/match/update_date")
    public ResponseEntity<String> updateMatchDate(@RequestParam String adminId,
                                                  @RequestParam String matchId,
                                                  @RequestParam String newMatchDate) {

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

    // public TournamentDTO creteTournament(String id, String tournamentName, List<MatchDTO> matches, List<TeamDTO> teams) {}
}
