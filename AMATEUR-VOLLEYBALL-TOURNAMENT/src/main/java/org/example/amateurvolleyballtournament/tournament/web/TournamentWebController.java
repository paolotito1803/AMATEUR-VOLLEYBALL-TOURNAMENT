package org.example.amateurvolleyballtournament.tournament.web;

import jakarta.servlet.http.HttpServletRequest;
import org.example.amateurvolleyballtournament.teams.StadiumDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tournament")
class TournamentWebController {
    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final UsersService usersService;

    TournamentWebController(TournamentService tournamentService, TeamService teamService, UsersService usersService) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
        this.usersService = usersService;
    }

    // Seeds a tournament and lists all tournaments
//    @GetMapping("/manager")
//    public String showManager(Model model) {
//        model.addAttribute("tournaments", tournamentService.getAllTournaments());
//        model.addAttribute("allTeams", teamService.getAllTeams());
//
//        return "tournament/manager";
//    }

    // Allows the admin to create tournaments
    @GetMapping("/admin_dashboard")
    public String showAdminDashboard(Model model) {
        // 1. Squadre per creare nuovi tornei
        model.addAttribute("allTeams", teamService.getAllTeams());
        // 2. Tornei esistenti per poterli gestire
        model.addAttribute("tournaments", tournamentService.getAllTournaments());

        return "tournament/admin_dashboard";
    }

    @GetMapping("/{id}/manage")
    public String showTournamentManagement(@PathVariable String id, Model model) {
        // 1. Fetches tournament
        var tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato"));

        // 2. Fetches referees
        var referees = usersService.listUsersByRole("REFEREE");

        // 3. Fetches stadium (picking them from existing teams)

        var stadiums = teamService.getAllTeams().stream()
                .map(team -> team.stadium())
                .filter(stadium -> stadium != null)
                .map(stadium -> stadium.name())
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .toList();

        model.addAttribute("tournament", tournament);
        model.addAttribute("referees", referees);
        model.addAttribute("stadiums", stadiums);

        return "tournament/tournament_details";
    }

    // Visualise tournament brackets
    @GetMapping("/{id}/bracket")
    public String showBracket(@PathVariable String id, Model model, HttpServletRequest request) {
        TournamentDTO tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        Map<Integer, List<MatchDTO>> matchesByStage = tournament.matches().stream()
                .sorted(Comparator.comparingInt(MatchDTO::matchIndex))
                .collect(Collectors.groupingBy(MatchDTO::stage));

        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByStage", matchesByStage);

        // Check for Role
        String authHeader = request.getHeader("Authorization");
        boolean isAdmin = false;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            isAdmin = usersService.isAdminFromToken(authHeader);
        }

        return isAdmin ? "tournament/admin_bracket_view" : "tournament/bracket_view";
    }

    @GetMapping("/{id}/admin_bracket")
    public String showAdminBracket(@PathVariable String id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        Map<Integer, List<MatchDTO>> matchesByStage = tournament.matches().stream()
                .sorted(Comparator.comparingInt(MatchDTO::matchIndex))
                .collect(Collectors.groupingBy(MatchDTO::stage));

        // Fetch referees
        var referees = usersService.listUsersByRole("REFEREE");

        // Fetch stadiums (picking them from existing teams)
        var stadiums = teamService.getAllTeams().stream()
                .map(team -> team.stadium())
                .filter(stadium -> stadium != null)
                .map(stadium -> stadium.name())
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .toList();

        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByStage", matchesByStage);
        model.addAttribute("referees", referees);
        model.addAttribute("stadiums", stadiums);

        return "tournament/admin_bracket_view";
    }

    @GetMapping("/{id}/bracket_view")
    public String showBracketView(@PathVariable String id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        Map<Integer, List<MatchDTO>> matchesByStage = tournament.matches().stream()
                .sorted(Comparator.comparingInt(MatchDTO::matchIndex))
                .collect(Collectors.groupingBy(MatchDTO::stage));

        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByStage", matchesByStage);

        return "tournament/bracket_view";
    }

    @GetMapping("/{id}/referee_view")
    public String showRefereeView(@PathVariable String id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        Map<Integer, List<MatchDTO>> matchesByStage = tournament.matches().stream()
                .sorted(Comparator.comparingInt(MatchDTO::matchIndex))
                .collect(Collectors.groupingBy(MatchDTO::stage));

        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByStage", matchesByStage);

        return "tournament/referee_tournament_view";
    }

    @GetMapping("/{id}/view")
    public String showView(@PathVariable String id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        Map<Integer, List<MatchDTO>> matchesByStage = tournament.matches().stream()
                .sorted(Comparator.comparingInt(MatchDTO::matchIndex))
                .collect(Collectors.groupingBy(MatchDTO::stage));

        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByStage", matchesByStage);

        return "tournament/tournament_view";
    }
}