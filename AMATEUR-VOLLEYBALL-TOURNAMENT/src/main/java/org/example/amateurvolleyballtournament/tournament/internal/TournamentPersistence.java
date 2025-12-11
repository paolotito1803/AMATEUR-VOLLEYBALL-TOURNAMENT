package org.example.amateurvolleyballtournament.tournament.internal;

import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
class TournamentPersistence {
    private List<TeamDTO> teams;
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;

    TournamentPersistence(List<TeamDTO> teams, TournamentRepository tournamentRepository, MatchRepository matchRepository) {
        this.teams = teams;
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
    }

//    @Transactional
//    public void save(MatchEntity match) { matchRepository.save(match); }
//
//    @Transactional
//    public void save(TournamentEntity tournament) { tournamentRepository.save(tournament); }

    public Iterable<TournamentEntity> findAll() {
        return tournamentRepository.findAll();
    }
}
