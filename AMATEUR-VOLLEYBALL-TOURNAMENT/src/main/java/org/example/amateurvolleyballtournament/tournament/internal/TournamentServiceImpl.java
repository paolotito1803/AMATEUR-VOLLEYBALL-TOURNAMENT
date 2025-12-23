package org.example.amateurvolleyballtournament.tournament.internal;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
class TournamentServiceImpl implements TournamentService {
    private final TeamService teamService;
    private final TournamentPersistence tournamentPersistence;
    private final MatchRepository matchRepository;
    private final UsersService usersService;

    public TournamentServiceImpl(TeamService teamService, TournamentPersistence tournamentPersistence, MatchRepository matchRepository, UsersService usersService) {
        this.teamService = teamService;
        this.tournamentPersistence = tournamentPersistence;
        this.matchRepository = matchRepository;
        this.usersService = usersService;
    }

    private void verifyAdmin() {
        // TODO: IMPLEMENT AUTH SECURITY
        // Something like:
        // String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        // if (!usersService.isAdmin(currentUserId)) {
        //     throw new IllegalArgumentException("Access Denied: User is not an Admin");
        // }
        // For now it's just allowed

        // Also: some of these don't require only admin, but maybe coach or referee, so should be considered individually
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
    public LocalDateTime getMatchDate(String matchId) {
        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalStateException("Match with id " + matchId + " does not exist");
        }

        // 'LocalDateTime::toString' to make it as a string
        return tournamentPersistence.getMatchDate(matchId)
                .orElseThrow(() -> new IllegalStateException("Date for match " + matchId + " is not set yet."));
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
                s.getStage(), s.getTeamA(), s.getTeamB(), s.getScoreA(), s.getScoreB(), s.getWinner(), s.getNextMatchId()));
    }

    // Check parameters for creation, fetch matches and teams and manage first stage of tournament
    @Override
    public void createTournament(String tournamentName, int maxTournamentTeams, List<String> teams) {
        verifyAdmin();

        if(teams.size() != maxTournamentTeams) {
            throw new IllegalArgumentException(("Number of matches should be as big as the max tournament matches"));
        }

        // Create tournament instance
        tournamentPersistence.createTournament(tournamentName, maxTournamentTeams, teams);

        // fetch id by new tournament name (as an alternative: return tournamentId upon creation)
        String tournamentId = tournamentPersistence.getTournamentIdByName(tournamentName)
                .orElseThrow(() -> new RuntimeException("Id for tournament name " + tournamentName + " not found."));

        // Create each match for the tournament
        for(int i = 0; i < teams.size(); i += 2) {
            createMatch(tournamentId, 1, teams.get(i), teams.get(i + 1));
        }
    }

    // Generate a match: assign team A, team B
    @Override
    public void createMatch(String tournamentId, int stage, String teamA, String teamB) {
        verifyAdmin();

        tournamentPersistence.createMatch(tournamentId, stage, teamA, teamB);
    }

    @Override
    public void updateMatchWinner(String matchId, String winner) {
        // Check for permissions
        verifyAdmin();

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match for match id " + matchId + " not found.");
        }

        // Fetch match tournament id
        String tournamentId = tournamentPersistence.getTournamentIdByMatchId(matchId);

        // Fetch teams from match
        List<String> matchTeams = tournamentPersistence.getMatchTeams(matchId)
                .orElseThrow(() -> new RuntimeException("Teams A and B for match " + matchId + " not found."));

        // Update match winner
        if(Objects.equals(winner, matchTeams.getFirst()) || Objects.equals(winner, matchTeams.getLast())) {
            tournamentPersistence.updateMatchWinner(matchId, winner);
        }
        else {
            throw new IllegalArgumentException("Winner " + winner + " does not match teamA" + matchTeams.getFirst() +
                    " or teamB " + matchTeams.getLast());
        }

        // Tournament stage management and eventually new match creation
        int maxStage = tournamentPersistence.getMatchesMaxStage(tournamentId);

        List<MatchEntity> matchInStageList = tournamentPersistence.getMatchesInTournamentStage(tournamentId, maxStage)
                .orElseThrow(() -> new RuntimeException("An error occurred while trying to update the tournament winner."));

        if(tournamentPersistence.getMatchStage(matchId) < maxStage) {
            for(MatchEntity match : matchInStageList) {
                // If it founds an empty team in the max stage matches, update one of the team with the winner
                if(match.getTeamA().isEmpty()) {
                    match.setTeamA(winner);
                    // Update winner's match with next match
                    tournamentPersistence.updateNextMatch(matchId, match.getMatchId());
                    break;
                }
                else if(match.getTeamB().isEmpty()) {
                    match.setTeamB(winner);
                    // Update winner's match with next match
                    tournamentPersistence.updateNextMatch(matchId, match.getMatchId());
                    break;
                }
            }
        }
        else {
            // Create a new match with a new maximum stage and the winner as one of the teams
            createMatch(tournamentId, maxStage + 1, winner, "");
        }
    }

    @Override
    public void updateTournamentName(String tournamentId, String tournamentName) {
        verifyAdmin();

        if(!tournamentPersistence.existsByTournamentId(tournamentId)) {
            throw new IllegalArgumentException("Tournament with id " + tournamentId + " not found.");
        }

        tournamentPersistence.updateTournamentName(tournamentId, tournamentName);
    }

    @Override
    public void updateTeams(List<String> teams, String tournamentId) {
        verifyAdmin();

        if(!tournamentPersistence.existsByTournamentId(tournamentId)) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " does not exist.");
        }

        if(teams.isEmpty()) {
            throw new IllegalArgumentException("Teams cannot be empty.");
        }

        tournamentPersistence.updateTeams(teams, tournamentId);
    }

    @Override
    public void updateMatchReferee(String matchId, String newReferee) {
        verifyAdmin();

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        tournamentPersistence.updateReferee(matchId, newReferee);
    }

    @Override
    public void updateMatchDate(String matchId, LocalDateTime newDate) {
        verifyAdmin();

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        tournamentPersistence.updateMatchDate(matchId, newDate);
    }

    @Override
    public void updateMatchFreeSeats(String matchId, int newFreeSeats) {
        verifyAdmin();

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        // ADD CONTROL FOR MAX SEATS
        tournamentPersistence.updateMatchFreeSeats(matchId, newFreeSeats);
    }

    @Override
    public void updateMatchStadium(String matchId, String stadium) {
        verifyAdmin();

        if(!tournamentPersistence.existsByMatchId(matchId)) {
            throw new IllegalArgumentException("Match " + matchId + " does not exist.");
        }

        tournamentPersistence.updateMatchStadium(matchId, stadium);
    }

    // TODO: Is it a useful function?
    @Override
    public List<TeamDTO> getTeams() {
        return teamService.getAllTeams();
    }

    @Override
    public boolean reserveSeat(String matchId) {
        return matchRepository.decrementFreeSeatsIfAvailable(matchId) == 1; // I expect JUST one row updated
    }
}
