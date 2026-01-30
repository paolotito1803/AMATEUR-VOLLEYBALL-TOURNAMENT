package org.example.amateurvolleyballtournament.tournament.web;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("/match/count")
    public int getMatchCount(@RequestParam String tournamentId) {
        return tournamentService.getMatchCount(tournamentId);
    }

    @GetMapping("/team/count")
    public int getTeamCount(@RequestParam String tournamentId) {
        return tournamentService.getTeamCount(tournamentId);
    }

    @GetMapping("/{tournamentId}/matches")
    public List<MatchDTO> getTournamentMatches(@PathVariable String tournamentId) {
        return tournamentService.getTournamentDetails(tournamentId)
                .map(TournamentDTO::matches)
                .orElse(List.of());
    }

    @PostMapping("/update_name")
    public ResponseEntity<String> updateTournamentName(@RequestBody TournamentRequests.UpdateTournamentNameRequest request) {
        tournamentService.updateTournamentName(request.tournamentId(), request.tournamentName());

        return ResponseEntity.ok("Tournament updated successfully");
    }

    // UpdateTeams
    @PostMapping
    public ResponseEntity<String> updateTeams(@RequestBody TournamentRequests.UpdateTeamsRequest request) {
        tournamentService.updateTeams(request.teams(), request.tournamentId());

        return ResponseEntity.ok("Teams updated successfully.");
    }

    @PostMapping("/match/update_referee")
    public ResponseEntity<String> updateMatchReferee(@RequestBody TournamentRequests.UpdateRefereeRequest request) {
        tournamentService.updateMatchReferee(request.matchId(), request.newReferee());

        return ResponseEntity.ok("Referee " + request.newReferee() + " for match " + request.matchId() + "updated successfully.");
    }

    // Keep Date or switch to String with date format
    // Accepts ISO Date String (like "2025-12-25T15:00:00")
    @PostMapping("/match/update_date")
    public ResponseEntity<String> updateMatchDate(@RequestBody TournamentRequests.UpdateDateRequest request) {

        tournamentService.updateMatchDate(request.matchId(), request.newDate());

        return ResponseEntity.ok("Match " + request.matchId() +
                " updated successfully with date " + request.newDate());
    }

    @PostMapping("/match/update_free_seats")
    public ResponseEntity<String> updateMatchFreeSeats(@RequestBody TournamentRequests.UpdateFreeSeatsRequest request) {
        tournamentService.updateMatchFreeSeats(request.matchId(), request.newFreeSeats());

        return ResponseEntity.ok("Free seats for match " + request.matchId() + "updated successfully to value "
                + request.newFreeSeats());
    }

    @PostMapping("/match/update_stadium")
    public ResponseEntity<String> updateMatchStadium(@RequestBody TournamentRequests.UpdateStadiumRequest request) {
        tournamentService.updateMatchStadium(request.matchId(), request.newStadium());
        return ResponseEntity.ok("Stadium updated successfully.");
    }

    @PostMapping("/match/update_scores")
    public  ResponseEntity<String> updateMatchScores(@RequestBody TournamentRequests.UpdateMatchScoresRequest request) {
        tournamentService.updateMatchScore(request.matchId(), request.scoreA(), request.scoreB());
        return ResponseEntity.ok("Match scores updated successfully.");
    }

    // Also automatically manage matches creation for first stage
    @PostMapping("/create_tournament")
    public ResponseEntity<String> createTournament(@RequestBody TournamentRequests.CreateTournamentRequest request) {
        tournamentService.createTournament(
                request.tournamentName(),
                request.teams()
        );

        return ResponseEntity.ok("Tournament " + request.tournamentName() + " created successfully.");
    }

    @PostMapping("/match/create_match")
    public ResponseEntity<String> createMatch(@RequestBody TournamentRequests.CreateMatchRequest request) {
        tournamentService.createMatch(
                request.tournamentId(),
                request.stage(),
                request.teamA(),
                request.teamB()
        );

        return ResponseEntity.ok("Match for tournament " + request.tournamentId() + " created successfully.");
    }

    // Update match winner and assign it to upper matches (or create a new stage)
    @PostMapping("/match/update_winner")
    public ResponseEntity<String> updateMatchWinner(@RequestBody TournamentRequests.UpdateWinnerRequest request) {
        tournamentService.updateMatchWinner(
                request.matchId(),
                request.winner()
        );

        return ResponseEntity.ok("Match winner " + request.winner() + " for match " + request.matchId() + "  updated successfully.");
    }

    @PostMapping("/match/referee_update_winner")
    public ResponseEntity<String> refUpdateMatchWinner(@RequestBody TournamentRequests.UpdateWinnerRequest request) {
        tournamentService.refUpdateMatchWinner(
                request.matchId(),
                request.winner()
        );

        return ResponseEntity.ok("Match winner " + request.winner() + " for match " + request.matchId() + "  updated successfully.");
    }

    @DeleteMapping("/delete/{tournamentId}")
    public ResponseEntity<String> deleteTournament(@PathVariable String tournamentId) {
        tournamentService.deleteTournament(tournamentId);

        return ResponseEntity.ok("Tournament deleted successfully.");
    }
}
