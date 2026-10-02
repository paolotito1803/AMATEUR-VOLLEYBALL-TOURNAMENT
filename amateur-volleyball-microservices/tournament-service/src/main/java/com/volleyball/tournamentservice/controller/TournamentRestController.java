package com.volleyball.tournamentservice.controller;

import com.volleyball.common.dto.MatchDTO;
import com.volleyball.common.dto.StadiumDTO;
import com.volleyball.common.dto.TournamentDTO;
import com.volleyball.common.security.RequireRole;
import com.volleyball.tournamentservice.service.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentRestController {

    private final TournamentService service;

    public TournamentRestController(TournamentService service) {
        this.service = service;
    }

    @GetMapping
    public List<TournamentDTO> getAll() {
        return service.getAllTournaments();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TournamentDTO> getDetails(@PathVariable String id) {
        return service.getTournamentDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{name}")
    public ResponseEntity<TournamentDTO> getByName(@PathVariable String name) {
        return service.getTournamentByName(name).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stadiums")
    public ResponseEntity<List<StadiumDTO>> getStadiums(@PathVariable String id) {
        List<StadiumDTO> stadiums = service.getTournamentStadiums(id);
        return ResponseEntity.ok(stadiums);
    }

    @PostMapping("/create")
    @RequireRole({"ADMIN"})
    public ResponseEntity<String> create(@RequestBody TournamentRequests.CreateTournamentRequest request) {
        try {
            service.createTournament(request.tournamentName(), request.teams());
            return ResponseEntity.ok("Tournament created");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.deleteTournament(id);
        return ResponseEntity.ok("Tournament deleted");
    }

    @PostMapping("/match/winner")
    @RequireRole({"REFEREE", "ADMIN"})
    public ResponseEntity<String> updateWinner(@RequestBody TournamentRequests.UpdateWinnerRequest request) {
        try {
            service.updateMatchWinner(request.matchId(), request.winner());
            return ResponseEntity.ok("Winner updated");
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Data error: " + e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/match/referee")
    @RequireRole({"ADMIN"})
    public ResponseEntity<String> updateReferee(@RequestBody TournamentRequests.UpdateRefereeRequest request) {
        try {
            service.updateMatchReferee(request.matchId(), request.newReferee());
            return ResponseEntity.ok("Referee updated");
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Data error: " + e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("Sever Error: " + e.getMessage());
        }
    }
    @PostMapping("/match/date")
    @RequireRole({"ADMIN"})
    public ResponseEntity<String> updateMatchDate(@RequestBody TournamentRequests.UpdateMatchDateRequest request) {
        try {
            service.updateMatchDate(request.matchId(), LocalDateTime.parse(request.date()));
            return ResponseEntity.ok("Match date updated");
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Data error: " + e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/match/score")
    @RequireRole({"REFEREE", "ADMIN"})
    public ResponseEntity<String> updateMatchScore(@RequestBody TournamentRequests.UpdateMatchScoresRequest request){
        try{
            service.updateMatchScore(request.matchId(), request.scoreA(), request.scoreB());
            return ResponseEntity.ok("Match score updated");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Data error: " + e.getMessage());
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    @GetMapping("match/{matchId}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable String matchId) {
        return service.findMatchById(matchId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/match/{matchId}/reserve")
    public ResponseEntity<String> reserveSeat(@PathVariable String matchId) {
        boolean success = service.reserveSeat(matchId);
        if (success) {
            return ResponseEntity.ok("Seat reserved");
        } else {
            return ResponseEntity.status(409).body("No seats available");
        }
    }

    @PostMapping("/match/stadium")
    @RequireRole({"ADMIN"})
    public ResponseEntity<String> assignMatchStadium(@RequestBody TournamentRequests.AssignStadiumRequest request) {
        service.assignStadiumToMatch(request.matchId(), request.newStadium());
        return ResponseEntity.ok("Stadium and Capacity assigned successfully.");
    }
    
    @GetMapping("/match/count")
    public int getMatchCount(@RequestParam String tournamentId) {
        return service.getMatchCount(tournamentId);
    }

    @GetMapping("/team/count")
    public int getTeamCount(@RequestParam String tournamentId) {
        return service.getTeamCount(tournamentId);
    }

    @GetMapping("/{tournamentId}/matches")
    public List<MatchDTO> getTournamentMatches(@PathVariable String tournamentId) {
        return service.getTournamentDetails(tournamentId)
                .map(TournamentDTO::matches)
                .orElse(List.of());
    }
}
