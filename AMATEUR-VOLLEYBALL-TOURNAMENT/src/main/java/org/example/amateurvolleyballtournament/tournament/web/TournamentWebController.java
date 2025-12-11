package org.example.amateurvolleyballtournament.tournament.web;

import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tournament")
class TournamentWebController {
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TeamService teamService;

    TournamentWebController(TournamentService tournamentService, TeamService teamService) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    @GetMapping("/manager")
    public String ShowTournaments(Model model) {
        model.addAttribute("tournament", tournamentService.getAllTournaments());
        return "tournament/manager";
    }
}

