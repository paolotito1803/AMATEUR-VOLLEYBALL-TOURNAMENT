package org.example.amateurvolleyballtournament.ticketing.web;

import org.example.amateurvolleyballtournament.ticketing.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tickets")
class TicketWebController {
    private final TicketService ticketService;

    TicketWebController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

//    @GetMapping("/manager")
//    public String showTicketsUser(Model model) {
//        model.addAttribute("tickets", ticketService.getAllTicketsByUserId("1"));
//        return "ticketing/manager";
//    }
//
//    @GetMapping("/my-tickets")
//    public String showMyTickets(Model model) {
//        return "ticketing/tickets";
//    }
}
