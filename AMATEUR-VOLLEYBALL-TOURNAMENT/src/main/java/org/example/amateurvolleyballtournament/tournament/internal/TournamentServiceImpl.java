package org.example.amateurvolleyballtournament.tournament.internal;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
class TournamentServiceImpl implements TournamentService {
    private final TeamService teamService;
    private final TournamentPersistence tournamentPersistence;
    private final UsersService usersService;

    public TournamentServiceImpl(TeamService teamService, TournamentPersistence tournamentPersistence, UsersService usersService) {
        this.teamService = teamService;
        this.tournamentPersistence = tournamentPersistence;
        this.usersService = usersService;
    }

    @Override
    public List<TournamentDTO> getAllTournaments() {
        List<TournamentEntity> entities = (List<TournamentEntity>) tournamentPersistence.findAll();
        return List.of();
    }

    @Override
    public String getMatchReferee(String matchId) {
        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalStateException("Match with id " + matchId + " does not exist");
        }

        return tournamentPersistence.getMatchReferee(matchId).orElseThrow(()  -> new IllegalStateException("Referee for match " + matchId + " does not exist"));
    }

    @Override
    public String getMatchDate(String matchId) {
        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalStateException("Match with id " + matchId + " does not exist");
        }

        return tournamentPersistence.getMatchDate(matchId).orElseThrow(()   -> new IllegalStateException("Date for match " + matchId + " does not exist"));
    }

    @Override
    public Optional<MatchDTO> getMatchInformation(String matchId) {
        return getMatchDTO(matchId);
    }

    @Override
    public String getMatchWinner(String matchId) {
        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalStateException("Match with id " + matchId + " does not exist");
        }

        return tournamentPersistence.getMatchWinner(matchId).orElseThrow(()  -> new IllegalStateException("Winner for match " + matchId + " does not exist"));
    }

    @Override
    public Optional<MatchDTO> getNextMatch(String matchId) {
        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalStateException("Match with id " + matchId + " does not exist");
        }

        String nextMatchId = tournamentPersistence.getNextMatch(matchId).orElseThrow(()  -> new IllegalStateException("Next match for match " + matchId + " does not exist"));

        return getMatchDTO(nextMatchId);
    }

    private Optional<MatchDTO> getMatchDTO(String nextMatchId) {
        if(!tournamentPersistence.existsByMatchId(nextMatchId)) {
            throw new IllegalStateException("Match with id " + nextMatchId + " does not exist");
        }

        return tournamentPersistence.findMatch(nextMatchId).map(s -> new MatchDTO(s.getMatchId(), s.getStadium(), s.getFreeSeats(), s.getMatchDate(), s.getReferee(),
                s.getPosition(), s.getStage(), s.getTeamA(), s.getTeamB(), s.getScoreA(), s.getScoreB(), s.getWinner(), s.getNextMatchId()));
    }

    // Check parameters for creation, fetch matches and teams and manage first stage of tournament
    @Override
    public void createTournament(String adminId, String tournamentName, List<String> matches, List<String> teams) {
        // TODO
    }

    // Generate a match: assign team A, team B, stadium, free seats, date ...
    @Override
    public void createMatch(String adminId, String stadium, int freeSeats, String matchDate, String referee, int position, int stage, String teamA, String teamB) {
        //TODO
    }

    @Override
    public void updateTeams(String adminId, List<String> teams, String tournamentId) {
        if(!usersService.isAdmin(adminId)) {
            throw new IllegalArgumentException("User " + adminId + " is not authorized to update the teams of the tournament.");
        }

        if(!tournamentPersistence.existsByTournamentId(tournamentId)) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " does not exist.");
        }

        if(teams.isEmpty()) {
            throw new IllegalArgumentException("Teams cannot be empty.");
        }

        tournamentPersistence.updateTeams(teams, tournamentId);
    }

    @Override
    public void updateMatchReferee(String adminId, String matchId, String newReferee) {
        if(!usersService.isAdmin(adminId)) {
            throw new IllegalArgumentException("User " + adminId + " is not authorized to update the match referee.");
        }

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        tournamentPersistence.updateReferee(matchId, newReferee);
    }

    @Override
    public void updateMatchDate(String adminId, String matchId, String newDate) {
        if(!usersService.isAdmin(adminId)) {
            throw new IllegalArgumentException("User " + adminId + " is not authorized to update the match date.");
        }

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        tournamentPersistence.updateMatchDate(matchId, newDate);
    }

    @Override
    public void updateMatchFreeSeats(String adminId, String matchId, int newFreeSeats) {
        if(!usersService.isAdmin(adminId)) {
            throw new IllegalArgumentException("User " + adminId + " is not authorized to update the match free seats.");
        }

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        // ADD CONTROL FOR MAX SEATS
        tournamentPersistence.updateMatchFreeSeats(matchId, newFreeSeats);
    }

    // TODO: Is it a useful function?
    @Override
    public List<TeamDTO> getTeams() {
        return teamService.getAllTeams();
    }
}
