package org.example.amateurvolleyballtournament;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// TO DELETE, JUST FOR TESTING THE SETUP
@Controller
public class TestHomeController {

    @GetMapping("/")
    public String home() {
        // This tells Spring: "Look for a file named 'index.html' in templates"
        return "index";
    }
}
