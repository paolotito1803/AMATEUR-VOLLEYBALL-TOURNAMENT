package org.example.amateurvolleyballtournament.teams.web;

// This is the JSON body used in the request (instead of passing a long list of parameters)
// It also subscribes to the DTO pattern we have used everywhere
public record RegisterTeamRequest(
        String coachId,
        String coachName,
        String teamName
) {}
