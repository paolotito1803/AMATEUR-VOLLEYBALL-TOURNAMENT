package org.example.amateurvolleyballtournament.teams.web;

import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teams")
class TeamWebController {

    private final TeamService teamService;
    private final UsersService usersService;

    TeamWebController(TeamService teamService, UsersService usersService) {
        this.teamService = teamService;
        this.usersService = usersService;
    }

//    // URL: http://localhost:8080/teams/manager
//    @GetMapping("/manager")
//    public String showManagerPage(Model model) {
//        // TODO: ADD PROPER AUTHORIZATION LOGIC
//        model.addAttribute("teams", teamService.getAllTeams());
//
//        return "teams/manager";
//    }
}
