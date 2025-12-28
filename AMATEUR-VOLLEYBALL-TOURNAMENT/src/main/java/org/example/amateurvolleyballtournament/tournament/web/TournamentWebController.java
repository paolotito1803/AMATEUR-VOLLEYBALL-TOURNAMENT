package org.example.amateurvolleyballtournament.tournament.web;

import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
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

    TournamentWebController(TournamentService tournamentService, TeamService teamService) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    // Seeds a tournament and lists all tournaments
    @GetMapping("/manager")
    public String showManager(Model model) {
        model.addAttribute("tournaments", tournamentService.getAllTournaments());
        model.addAttribute("allTeams", teamService.getAllTeams());

        return "tournament/manager";
    }

    // Visualise tournament brackets
    @GetMapping("/{id}/bracket")
    public String showBracket(@PathVariable String id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        Map<Integer, List<MatchDTO>> matchesByStage = tournament.matches().stream()
                .sorted(Comparator.comparingInt(MatchDTO::matchIndex))
                .collect(Collectors.groupingBy(MatchDTO::stage));

        model.addAttribute("tournament", tournament);
        model.addAttribute("matchesByStage", matchesByStage);

        return "tournament/bracket";
    }
}