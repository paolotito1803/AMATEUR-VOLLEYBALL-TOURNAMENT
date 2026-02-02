package org.example.amateurvolleyballtournament;

import org.example.amateurvolleyballtournament.teams.PlayerDTO;
import org.example.amateurvolleyballtournament.teams.StadiumDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.teams.VolleyballRole;
import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.InputUserDTO;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("!test") // don't want this running during JUnit tests
public class GlobalSeeder implements CommandLineRunner {

    private final UsersService usersService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final TicketService ticketService;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.surname}")
    private String adminSurname;

    public GlobalSeeder(UsersService usersService, TeamService teamService,
                        TournamentService tournamentService, TicketService ticketService) {
        this.usersService = usersService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
        this.ticketService = ticketService;
    }

    @Override
    public void run(String... args) {
        System.out.println("==================================");
        System.out.println("   GLOBAL SEEDER: STARTING");
        System.out.println("==================================");

        try {
            seedUsers();
            seedTeams();
            seedTournaments();
            seedTickets();
            System.out.println("==================================");
            System.out.println("   GLOBAL SEEDER: COMPLETED");
            System.out.println("==================================");
        } catch (Exception e) {
            System.err.println("SEEDING ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 1. USER MODULE
    private void seedUsers() {
        System.out.println("-> Seeding Users...");

        // Admin
        createUser("id_admin_1", adminUsername, adminSurname, adminEmail, "ADMIN", adminPassword);

        // Coaches
        createUser("id_coach_1", "Coach", "Fire", "coach1@mail.com", "COACH", "pwd");
        createUser("id_coach_2", "Coach", "Ice", "coach2@mail.com", "COACH", "pwd");
        createUser("id_coach_3", "Coach", "Earth", "coach3@mail.com", "COACH", "pwd");
        createUser("id_coach_4", "Coach", "Wind", "coach4@mail.com", "COACH", "pwd");

        // Referees
        createUser("id_ref_1", "John", "Doe", "ref1@mail.com", "REFEREE", "pwd");
        createUser("id_ref_2", "Jane", "Smith", "ref2@mail.com", "REFEREE", "pwd");

        // Viewers/Players
        createUser("id_viewer_1", "Luigi", "Watch", "viewer1@mail.com", "VIEWER", "pwd");
        createUser("id_player_1", "Mario", "Spike", "player1@mail.com", "PLAYER", "pwd");
    }

    private void createUser(String id, String name, String surname, String email, String role, String pwd) {
        try {
            usersService.createUser(new InputUserDTO(id, name, surname, pwd, email, role));
        } catch (Exception e) {
            // System.out.println("User " + email + " already exists.");
        }
    }

    // 2. TEAMS MODULE
    private void seedTeams() {
        System.out.println("-> Seeding Teams...");

        createFullTeam("id_coach_1", "Coach Fire", "Fire Dragons", "Volcano Arena", 50);
        createFullTeam("id_coach_2", "Coach Ice", "Ice Walkers", "Glacier Dome", 20);
        createFullTeam("id_coach_3", "Coach Earth", "Earth Golems", "Canyon Court", 100);
        createFullTeam("id_coach_4", "Coach Wind", "Wind Flyers", "Sky Gym", 100);
    }

    private void createFullTeam(String coachId, String coachName, String teamName, String stadiumName, int seats) {
        try {
            // Register Team
            teamService.registerTeam(coachId, coachName, teamName);

            // Setup Stadium
            teamService.updateStadium(coachId, new StadiumDTO(stadiumName, seats));

            // Add Players
            for (int i = 1; i <= 6; i++) {
                VolleyballRole role = (i == 1) ? VolleyballRole.SETTER : (i == 6 ? VolleyballRole.LIBERO : VolleyballRole.PLAYER);
                teamService.addPlayer(coachId, new PlayerDTO(teamName + " Player " + i, i, role));
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            // System.out.println("Team " + teamName + " already exists.");
        }
    }

    // 3. TOURNAMENT MODULE
    private void seedTournaments() {
        System.out.println("-> Seeding Tournaments...");

        String tourneyName = "Elemental Championship 2025";

        if (tournamentService.getTournamentByName(tourneyName).isEmpty()) {
            List<String> teams = List.of("Fire Dragons", "Ice Walkers", "Earth Golems", "Wind Flyers");
            try {
                tournamentService.createTournament(tourneyName, teams);
                //System.out.println("   + Created Tournament: " + tourneyName);
            } catch (Exception e) {
                System.err.println("   - Error creating tournament: " + e.getMessage());
            }
        }

        try {
            Optional<TournamentDTO> tOpt = tournamentService.getTournamentByName(tourneyName);
            if (tOpt.isPresent()) {
                TournamentDTO t = tOpt.get();

                MatchDTO m1 = t.matches().stream()
                        .filter(m -> m.matchIndex() == 1)
                        .findFirst()
                        .orElse(null);

                if (m1 != null) {
                    tournamentService.updateMatchReferee(m1.matchId(), "Doe John");
                    tournamentService.updateMatchDate(m1.matchId(), LocalDateTime.now().plusDays(1).withHour(18).withMinute(0));

                    // Assign Stadium
                    teamService.getAllTeams().stream()
                            .filter(team -> team.name().equals(m1.teamA()))
                            .findFirst()
                            .ifPresent(hostTeam -> {
                                if (hostTeam.stadium() != null) {
                                    tournamentService.updateMatchStadium(m1.matchId(), hostTeam.stadium().name());
                                    //System.out.println("   + Updated Match #1 Stadium to: " + hostTeam.stadium().name());
                                }
                            });
                }
            }
        } catch (Exception e) {
            System.err.println("   - Error configuring match details: " + e.getMessage());
        }
    }

    // 4. TICKETING MODULE
    private void seedTickets() {
        System.out.println("-> Seeding Tickets...");

        String viewerId = "id_viewer_1";

        // Find a valid match with seats
        List<TournamentDTO> tournaments = tournamentService.getAllTournaments();
        if (tournaments.isEmpty()) return;

        TournamentDTO t = tournaments.getFirst();

        t.matches().stream()
                .filter(m -> m.matchIndex() == 1)
                .findFirst()
                .ifPresent(match -> {
                    try {
                        if (ticketService.getAllTicketsByUserId(viewerId).isEmpty()) {
                            ticketService.addTicket(5, viewerId, match.matchId());
                            //System.out.println("Ticket bought for " + viewerId + " on match " + match.matchId());
                        }
                        else {
                            // System.out.println("Ticket already bought for " + viewerId + " on match " + match.matchId());
                        }
                    } catch (Exception e) {
                        System.err.println("Ticket Seeding Failed: " + e.getMessage());
                    }
                });
    }
}