package org.example.amateurvolleyballtournament.teams.internal;

import org.example.amateurvolleyballtournament.teams.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class DataSeeder implements CommandLineRunner {

    private final TeamService teamService;

    DataSeeder(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public void run(String... args) {
        // Only run if DB is empty
        if (!teamService.getAllTeams().isEmpty()) return;

        System.out.println("Seeding Demo Data...");

        String coachId = "coach_mario";
        String coachName = "Mario Rossi";

        // 1. Create Team
        teamService.registerTeam(coachId, coachName, "Thunder Spikes");

        // 2. Add Stadium
        teamService.updateStadium(coachId, new StadiumDTO("PalaVolley", 500));

        // 3. Add Players
        teamService.addPlayer(coachId, new PlayerDTO("Luigi", 10, VolleyballRole.SETTER));
        teamService.addPlayer(coachId, new PlayerDTO("Peach", 7, VolleyballRole.LIBERO));
        teamService.addPlayer(coachId, new PlayerDTO("Toad", 1, VolleyballRole.PLAYER));

        System.out.println("Data Seeded! Check /teams via Controller.");
    }
}
