package org.example.amateurvolleyballtournament.tournament.internal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.example.amateurvolleyballtournament.users.internal.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@Transactional
class TournamentServiceImpl implements TournamentService {
    private final TeamService teamService;
    private final TournamentPersistence tournamentPersistence;
    private final MatchRepository matchRepository;
    private final UsersService userService;

    TournamentServiceImpl(TeamService teamService, TournamentPersistence tournamentPersistence, MatchRepository matchRepository, UsersService usersService) {
        this.teamService = teamService;
        this.tournamentPersistence = tournamentPersistence;
        this.matchRepository = matchRepository;
        this.userService = usersService;
    }

    private void verifyAdmin() {
        try {
            // Get current HTTP request
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            // Fix for seeder
            // If no HTTP request: execute
            if (attributes == null) {
                log.warn("verifyAdmin: Executing outside request context (seeder/scheduled task allowed)");
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("verifyAdmin: No valid Authorization header found");
                throw new IllegalArgumentException("Access Denied: No authentication token provided");
            }

            // Use UserService to verify Admin
            if (!userService.isAdminFromToken(authHeader)) {
                String role = userService.extractRoleFromToken(authHeader);
                log.warn("verifyAdmin: User with role '{}' attempted admin action", role);
                throw new IllegalArgumentException("Access Denied: User is not an Admin");
            }

            log.debug("verifyAdmin: Admin access granted");

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verifying admin: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Access Denied: Authentication failed");
        }
    }

    @Override
    public List<TournamentDTO> getAllTournaments() {
        List<TournamentEntity> entities = (List<TournamentEntity>) tournamentPersistence.findAll();

        return entities.stream()
                .map(e -> {
                    List<MatchEntity> matches = tournamentPersistence.getMatchesByTournament(e.getTournamentId());

                    List<MatchDTO> matchDTOS = matches.stream()
                            .map(this::toMatchDTO)
                            .toList();

                    return new TournamentDTO(
                            e.getTournamentId(),
                            e.getTournamentName(),
                            matchDTOS,
                            getTeams(e.getTournamentId())
                    );
                })
                .toList();
    }

    private MatchDTO toMatchDTO(MatchEntity m) {
        return new MatchDTO(
                m.getMatchId(),
                m.getStadium(),
                m.getFreeSeats(),
                m.getMatchDate(),
                m.getReferee(),
                m.getStage(),
                m.getMatchIndex(),
                m.getTeamA(),
                m.getTeamB(),
                m.getScoreA(),
                m.getScoreB(),
                m.getWinner(),
                m.getNextMatchId()
        );
    }

    @Override
    public Optional<TournamentDTO> getTournamentDetails(String tournamentId) {
        Optional<TournamentEntity> tournamentOpt = tournamentPersistence.findById(tournamentId);

        if (tournamentOpt.isEmpty()) {
            return Optional.empty();
        }

        TournamentEntity entity = tournamentOpt.get();

        List<MatchEntity> matches = tournamentPersistence.getMatchesByTournament(tournamentId);

        List<MatchDTO> matchDTOs = matches.stream()
                .map(this::toMatchDTO)
                .toList();

        return Optional.of(new TournamentDTO(
                entity.getTournamentId(),
                entity.getTournamentName(),
                matchDTOs,
                getTeams(tournamentId)
        ));
    }

    @Override
    public Optional<TournamentDTO> getTournamentByName(String name) {
        Optional<String> tournamentId = tournamentPersistence.getTournamentIdByName(name);

        return tournamentId.flatMap(this::getTournamentDetails);
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
        return tournamentPersistence.findMatch(matchId).map(this::toMatchDTO);
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

        return tournamentPersistence.getNextMatch(matchId)
                .flatMap(tournamentPersistence::findMatch)
                .map(this::toMatchDTO);
    }

    private Optional<MatchDTO> getMatchDTO(String nextMatchId) {
        if(!tournamentPersistence.existsByMatchId(nextMatchId)) {
            throw new IllegalStateException("Match with id " + nextMatchId + " does not exist");
        }

        return tournamentPersistence.findMatch(nextMatchId).map(s -> new MatchDTO(
                s.getMatchId(),
                s.getStadium(),
                s.getFreeSeats(),
                s.getMatchDate(),
                s.getReferee(),
                s.getStage(),
                s.getMatchIndex(),
                s.getTeamA(),
                s.getTeamB(),
                s.getScoreA(),
                s.getScoreB(),
                s.getWinner(),
                s.getNextMatchId()
        ));
    }

    @Override
    public int getMatchCount(String tournamentId) {
        return tournamentPersistence.getMatchesByTournament(tournamentId).size();
    }

    @Override
    public int getTeamCount(String tournamentId) {
        return tournamentPersistence.findById(tournamentId).orElseThrow(() -> new IllegalStateException("Tournament not found")).getTeams().size();
    }

    // Check parameters for creation, fetch matches and teams builds the tournament structure
    @Override
    public void createTournament(String tournamentName, List<String> teams) {
        verifyAdmin();

        // Checks
        if (teams.size() < 2) {
            throw new IllegalArgumentException("A tournament requires at least 2 teams.");
        }

        Set<String> uniqueTeams = new HashSet<>(teams);
        if (uniqueTeams.size() != teams.size()) {
            throw new IllegalArgumentException("Duplicate teams are not allowed.");
        }

        List<String> validTeamNames = teamService.getAllTeams().stream()
                .map(TeamDTO::name)
                .toList();
        for (String inputTeam : teams) {
            if (!validTeamNames.contains(inputTeam)) {
                throw new IllegalArgumentException("Team '" + inputTeam + "' does not exist in the Teams database.");
            }
        }

        // Handle not power of 2 teams
        List<String> tournamentTeams = new ArrayList<>(teams);

        // Find next power of 2
        int targetSize = 1;
        while (targetSize < tournamentTeams.size()) {
            targetSize *= 2;
        }

        // Fill empty spots with "BYE", (so tournaments not with just powers of 2 teams can be set)
        while (tournamentTeams.size() < targetSize) {
            tournamentTeams.add("BYE");
        }

        tournamentPersistence.createTournament(tournamentName, targetSize, tournamentTeams);
        String tournamentId = tournamentPersistence.getTournamentIdByName(tournamentName)
                .orElseThrow(() -> new RuntimeException("Error creating tournament."));

        // Build pyramid bracket
        List<MatchEntity> roundOneMatches = new ArrayList<>();
        List<MatchEntity> currentRoundMatches = new ArrayList<>();
        int currentStage = 1;
        int globalMatchIndex = 1;

        // Build first level of the tournament
        for (int i = 0; i < tournamentTeams.size(); i += 2) {
            String teamA = tournamentTeams.get(i);
            String teamB = tournamentTeams.get(i + 1);

            MatchEntity match = tournamentPersistence.createMatch(tournamentId, currentStage,
                    globalMatchIndex++, teamA, teamB);
            currentRoundMatches.add(match);
            roundOneMatches.add(match);
        }

        // Build upper levels of the tournament
        while (currentRoundMatches.size() > 1) {
            List<MatchEntity> nextRoundMatches = new ArrayList<>();
            currentStage++;

            for (int i = 0; i < currentRoundMatches.size(); i += 2) {
                MatchEntity match1 = currentRoundMatches.get(i);
                MatchEntity match2 = currentRoundMatches.get(i + 1);

                MatchEntity parentMatch = tournamentPersistence.createMatch(tournamentId, currentStage,
                        globalMatchIndex++, "", "");

                match1.setNextMatchId(parentMatch.getMatchId());
                match2.setNextMatchId(parentMatch.getMatchId());

                tournamentPersistence.saveMatch(match1);
                tournamentPersistence.saveMatch(match2);

                nextRoundMatches.add(parentMatch);
            }
            currentRoundMatches = nextRoundMatches;
        }

        // Processes empty spots and kickstart the auto wins
        for (MatchEntity match : roundOneMatches) {
            boolean teamAIsBye = "BYE".equals(match.getTeamA());
            boolean teamBIsBye = "BYE".equals(match.getTeamB());

            if (teamAIsBye && teamBIsBye) {
                processMatchResult(match.getMatchId(), "BYE");
            } else if (teamBIsBye) {
                processMatchResult(match.getMatchId(), match.getTeamA());
            } else if (teamAIsBye) {
                processMatchResult(match.getMatchId(), match.getTeamB());
            }
        }
    }

    // Generate a match: assign team A, team B
    @Override
    public void createMatch(String tournamentId, int stage, String teamA, String teamB) {
        verifyAdmin();

        tournamentPersistence.createMatch(tournamentId, stage, 0, teamA, teamB);
    }

    @Override
    public void updateMatchWinner(String matchId, String winner) {
        verifyAdmin();

        if ("BYE".equalsIgnoreCase(winner)) {
            throw new IllegalArgumentException("Cannot declare BYE as a winner.");
        }

        processMatchResult(matchId, winner);
    }

    @Override
    public void refUpdateMatchWinner(String matchId, String winner) {

        if ("BYE".equalsIgnoreCase(winner)) {
            throw new IllegalArgumentException("Cannot declare BYE as a winner.");
        }

        processMatchResult(matchId, winner);
    }

    @Override
    public void updateMatchScore(String matchId, int scoreA, int scoreB) {
        MatchEntity entity = tournamentPersistence.findMatch(matchId)
                .orElseThrow(() -> new RuntimeException("Error getting match with matchId: " + matchId));
        entity.setScoreA(scoreA);
        entity.setScoreB(scoreB);

        tournamentPersistence.saveMatch(entity);
    }

    private void processMatchResult(String matchId, String winner) {
        // Load Match
        MatchDTO currentMatch = getMatchInformation(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match " + matchId + " not found."));

        // Cannot have multiple winners
        if (currentMatch.winner() != null && !currentMatch.winner().isEmpty()) {
            throw new IllegalStateException("Match " + matchId + " already has a winner: " + currentMatch.winner());
        }

        // Cannot declare a winner if one of the teams is missing
        if (currentMatch.teamA() == null || currentMatch.teamA().isEmpty() ||
                currentMatch.teamB() == null || currentMatch.teamB().isEmpty()) {
            throw new IllegalStateException("Match is not ready yet. Waiting for opponents.");
        }

        // Validate Winner
        boolean isTeamA = winner.equals(currentMatch.teamA());
        boolean isTeamB = winner.equals(currentMatch.teamB());

        if (!isTeamA && !isTeamB) {
            throw new IllegalArgumentException("Team " + winner + " did not play in match " + matchId);
        }

        // Save Winner
        tournamentPersistence.updateMatchWinner(matchId, winner);

        // Move to Next Match
        String nextMatchId = currentMatch.nextMatch();

        if (nextMatchId != null && !nextMatchId.isEmpty()) {
            MatchEntity nextMatch = tournamentPersistence.getMatchOrThrow(nextMatchId);

            String newTeamA = nextMatch.getTeamA();
            String newTeamB = nextMatch.getTeamB();

            // Fill empty slot
            if (newTeamA == null || newTeamA.isEmpty()) {
                newTeamA = winner;
            } else if (newTeamB == null || newTeamB.isEmpty()) {
                newTeamB = winner;
            } else {
                throw new IllegalStateException("Next match " + nextMatchId + " is already full!");
            }

            tournamentPersistence.updateMatchTeams(nextMatchId, newTeamA, newTeamB);

            String autoWinner = findAutoWin(newTeamA, newTeamB);

            // If found an automatic winner -> recursion
            if (autoWinner != null) {
                processMatchResult(nextMatchId, autoWinner);
            }
        }
    }

    private static String findAutoWin(String newTeamA, String newTeamB) {
        String autoWinner = null;

        // Case 1: BYE vs BYE -> BYE advances
        if ("BYE".equals(newTeamA) && "BYE".equals(newTeamB)) {
            autoWinner = "BYE";
        }
        // Case 2: Real Team vs BYE -> Real Team advances
        // (Check if A is Real and B is BYE)
        else if (newTeamA != null && !newTeamA.isEmpty() && !"BYE".equals(newTeamA)
                && "BYE".equals(newTeamB)) {
            autoWinner = newTeamA;
        }
        // Case 3: BYE vs Real Team -> Real Team advances
        // (Check if B is Real and A is BYE)
        else if (newTeamB != null && !newTeamB.isEmpty() && !"BYE".equals(newTeamB)
                && "BYE".equals(newTeamA)) {
            autoWinner = newTeamB;
        }
        return autoWinner;
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


        int capacity = teamService.getStadiumByName(stadium).orElseThrow(
                () -> new RuntimeException("No stadium found with this name")).maxSeats();
        tournamentPersistence.updateMatchStadium(matchId, stadium);
        tournamentPersistence.updateMatchFreeSeats(matchId, capacity);
    }

    @Override
    public List<TeamDTO> getTeams(String tournamentId) {
        List<TeamDTO> teamDTO = teamService.getAllTeams();
        TournamentEntity entity = tournamentPersistence.findById(tournamentId).orElseThrow(() -> new IllegalArgumentException("Tournament with id " + tournamentId + " not found."));
        List<String> teamStringList = entity.getTeams();

        List<TeamDTO> teamDTOList = new ArrayList<>();

        for(TeamDTO team : teamDTO) {
            for(String teamName : teamStringList) {
                if(team.name().equals(teamName)) {
                    teamDTOList.add(team);
                }
            }
        }

        return teamDTOList;
    }

    @Override
    public void deleteTournament(String tournamentId) {
        verifyAdmin();

        if (!tournamentPersistence.existsByTournamentId(tournamentId)) {
            throw new IllegalArgumentException("Tournament with id " + tournamentId + " does not exist.");
        }

        tournamentPersistence.deleteTournament(tournamentId);
    }

    @Override
    public boolean reserveSeat(String matchId) {
        return matchRepository.decrementFreeSeatsIfAvailable(matchId) == 1; // I expect JUST one row updated
    }
}
