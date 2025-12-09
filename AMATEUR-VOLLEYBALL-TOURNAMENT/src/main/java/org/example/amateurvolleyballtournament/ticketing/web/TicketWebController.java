package org.example.amateurvolleyballtournament.ticketing.web;

import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/tickets")
class TicketWebController {
    private TicketService ticketService;

    TicketWebController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/admin")
    public String showTicketsUser(Model model) {
        model.addAttribute(ticketService.getAllTicketsByUserId("ADMIN"));
        return "tickets/admin";
    }
}
