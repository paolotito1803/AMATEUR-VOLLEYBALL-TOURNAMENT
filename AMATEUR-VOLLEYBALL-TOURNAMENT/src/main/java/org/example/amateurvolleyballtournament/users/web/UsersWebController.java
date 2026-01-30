package org.example.amateurvolleyballtournament.users.web;

import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
class UsersWebController {

    private final UsersService usersService;
    private final TeamService teamService;
    private final TournamentService tournamentService;

    UsersWebController(UsersService usersService, TeamService teamService, TournamentService tournamentService) {
        this.usersService = usersService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "users/login";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        return "users/register";
    }

    @GetMapping("coach_dashboard")
    public String showCoachDashboard(Model model) {
        return "users/CoachDashboard";
    }

    @GetMapping("viewer_dashboard")
    public String showViewerDashboard(Model model) {
        return "users/ViewerDashboard";
    }
}
