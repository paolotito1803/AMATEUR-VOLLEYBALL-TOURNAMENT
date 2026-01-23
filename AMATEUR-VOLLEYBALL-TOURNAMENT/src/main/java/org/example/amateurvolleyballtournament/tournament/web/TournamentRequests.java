package org.example.amateurvolleyballtournament.tournament.web;

import java.time.LocalDateTime;
import java.util.List;

public class TournamentRequests {

    private TournamentRequests() {}

    public record CreateTournamentRequest(String tournamentName, List<String> teams) {}

    public record CreateMatchRequest(String tournamentId, int stage, String teamA, String teamB) {}

    public record UpdateWinnerRequest(String matchId, String winner) {}

    public record UpdateDateRequest(String matchId, LocalDateTime newDate) {}

    public record UpdateTeamsRequest(String tournamentId, List<String> teams) {}

    public record UpdateRefereeRequest(String matchId, String newReferee) {}

    public record UpdateStadiumRequest(String matchId, String newStadium) {}

    public record UpdateFreeSeatsRequest(String matchId, int newFreeSeats) {}

    public record UpdateTournamentNameRequest(String tournamentId, String tournamentName) {}

    public record UpdateMatchScoresRequest(String matchId, int scoreA, int scoreB) {}
}