package org.example.amateurvolleyballtournament.users.web;

import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
class UsersWebController {

    private final UsersService usersService;

    UsersWebController(UsersService usersService) {
        this.usersService = usersService;
    }

    // URL: http://localhost:8080/users/manager
    @GetMapping("/manager")
    public String showManagerPage(Model model) {
        // TODO: aggiungi logica di sicurezza per admin
        model.addAttribute("users", usersService.getAllUsers());
        return "users/users"; // template Thymeleaf: templates/users/manager.html
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "users/login";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        return "users/register";
    }

    @GetMapping("referee_dashboard")
    public String showRefereeDashboard(Model model) {
        return "users/RefereeDashboard";
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
