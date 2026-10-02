package com.volleyball.ticketingservice.integration;

import com.volleyball.common.dto.MatchDTO;
import com.volleyball.common.dto.TournamentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class TournamentIntegration {

    private final RestClient restClient;

    public TournamentIntegration(RestClient.Builder builder, 
                                 @Value("${app.services.tournament-url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    public boolean reserveSeat(String matchId) {
        try {
            return restClient.post()
                    .uri("/api/tournaments/match/{matchId}/reserve", matchId)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (Exception e) { // like 409 or 404
            return false;
        }
    }

    public List<TournamentDTO> getAllTournaments() {
        try {
            return restClient.get()
                    .uri("/api/tournaments")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TournamentDTO>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public Optional<MatchDTO> getMatch(String matchId) {
        try {
            MatchDTO r = restClient.get()
                    .uri("/api/tournaments/match/{matchId}",matchId)
                    .retrieve().body(MatchDTO.class);
            return Optional.ofNullable(r);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found");
        }
    }
}
