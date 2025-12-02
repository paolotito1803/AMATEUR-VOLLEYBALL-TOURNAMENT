package org.example.amateurvolleyballtournament.tournament;

public record GroupsDTO(String groupId, int position, int stage, String teamA, String teamB, int scoreA, int scoreB, String winner, String nextGroupId) {
}
