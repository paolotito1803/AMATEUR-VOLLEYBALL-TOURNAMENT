package org.example.amateurvolleyballtournament.tournament.internal;

import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Component
class TournamentPersistence {
    private List<String> teams;
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;

    TournamentPersistence(List<String> teams, TournamentRepository tournamentRepository, MatchRepository matchRepository) {
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

    public Optional<MatchEntity> findMatch(String matchId) {
        return matchRepository.findById(matchId);
    }

    public int getMaxTournamentTeams(String tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament with id " + tournamentId + " not found."));

        return tournamentEntity.getMaxTournamentTeams();
    }

    public Optional<String> getTournamentIdByName(String tournamentName) {
        TournamentEntity tournamentEntity = tournamentRepository.findByTournamentName(tournamentName)
                .orElseThrow(() -> new RuntimeException("Tournament with name " + tournamentName + " not found."));

        return Optional.ofNullable(tournamentEntity.getTournamentId());
    }

    public Optional<String> getMatchReferee(String matchId) {
        MatchEntity matchEntity = this.getMatchOrThrow(matchId);
        return Optional.ofNullable(matchEntity.getReferee());
    }

    public Optional<String> getMatchDate(String matchId) {
        MatchEntity matchEntity = this.getMatchOrThrow(matchId);
        return Optional.ofNullable(matchEntity.getMatchDate());
    }

    public Optional<String> getNextMatch(String matchId) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " +  matchId + " not found"));

        return Optional.ofNullable(matchEntity.getNextMatchId());
    }

    public Optional<String> getMatchWinner(String matchId) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));

        return Optional.ofNullable(matchEntity.getWinner());
    }

    public Optional<TournamentEntity> updateTeams(List<String> teamsId, String tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow(() -> new RuntimeException("Tournament with id " + tournamentId + " not found"));
        tournamentEntity.setTeams(teams);

        tournamentRepository.save(tournamentEntity);

        return Optional.of(tournamentEntity);
    }

    public Optional<MatchEntity> updateReferee(String matchId, String referee) {
           MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
           matchEntity.setReferee(referee);

           matchRepository.save(matchEntity);

           return Optional.of(matchEntity);
    }

    public Optional<MatchEntity> updateMatchDate(String matchId, String date) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
        matchEntity.setMatchDate(date);

        matchRepository.save(matchEntity);

        return Optional.of(matchEntity);
    }

    public Optional<MatchEntity> updateMatchFreeSeats(String matchId, int freeSeats) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
        matchEntity.setFreeSeats(freeSeats);

        matchRepository.save(matchEntity);

        return Optional.of(matchEntity);
    }

    public void createMatch(String torunamentId, int stage, String teamA, String teamB) {
        MatchEntity matchEntity = new MatchEntity(torunamentId, "", 0, "", "", stage, teamA, teamB, 0, 0, "", "");

        matchRepository.save(matchEntity);
    }

    public boolean existsByTournamentId(String tournamentId) {
        return tournamentRepository.existsById(tournamentId);
    }

    public boolean existsByMatchId(String matchId) {
        return matchRepository.existsById(matchId);
    }

    public MatchEntity getMatchOrThrow(String matchId) {
       return matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
    }
}
