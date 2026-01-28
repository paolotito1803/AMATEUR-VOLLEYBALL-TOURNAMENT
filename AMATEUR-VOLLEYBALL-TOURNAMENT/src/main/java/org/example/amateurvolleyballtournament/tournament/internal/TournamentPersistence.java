package org.example.amateurvolleyballtournament.tournament.internal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
class TournamentPersistence {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;

    TournamentPersistence(TournamentRepository tournamentRepository, MatchRepository matchRepository) {
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

    public Optional<TournamentEntity> findById(String id) {
        return tournamentRepository.findById(id);
    }

    public List<MatchEntity> getMatchesByTournament(String tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    public int getMaxTournamentTeams(String tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament with id " + tournamentId + " not found."));

        return tournamentEntity.getMaxTournamentTeams();
    }

    public Optional<String> getTournamentIdByName(String tournamentName) {
        return tournamentRepository.findByTournamentName(tournamentName)
                .map(TournamentEntity::getTournamentId);
    }

    public MatchEntity getMatchOrThrow(String matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
    }

    public Optional<List<String>> getMatchTeams(String matchId) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(
                () -> new RuntimeException("Match with id " + matchId + " not found."));

        List<String> teamList = new ArrayList<String>();
        teamList.add(matchEntity.getTeamA());
        teamList.add(matchEntity.getTeamB());

        return Optional.of(teamList);
    }

    public int getMatchesMaxStage(String tournamentId) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new RuntimeException("Tournament " + tournamentId + " not found");
        }
        return matchRepository.findMaxStageByTournamentId(tournamentId);
    }

    public List<MatchEntity> getMatchesInTournamentStage(String tournamentId, int stage) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new RuntimeException("Tournament " + tournamentId + " not found");
        }
        return matchRepository.findByTournamentIdAndStage(tournamentId, stage);
    }

    public Optional<String> getMatchReferee(String matchId) {
        return matchRepository.findById(matchId).map(MatchEntity::getReferee);
    }
    public Optional<LocalDateTime> getMatchDate(String matchId) {
        return matchRepository.findById(matchId).map(MatchEntity::getMatchDate);
    }
    public Optional<String> getMatchWinner(String matchId) {
        return matchRepository.findById(matchId).map(MatchEntity::getWinner);
    }
    public Optional<String> getNextMatch(String matchId) {
        return matchRepository.findById(matchId).map(MatchEntity::getNextMatchId);
    }
    public String getTournamentIdByMatchId(String matchId) {
        return getMatchOrThrow(matchId).getTournamentId();
    }
    public int getMatchStage(String matchId) {
        return getMatchOrThrow(matchId).getStage();
    }

    public void updateMatchWinner(String matchId, String winner) {
        MatchEntity match = getMatchOrThrow(matchId);
        match.setWinner(winner);
        matchRepository.save(match);
    }

    public void updateTeams(List<String> teams, String tournamentId) {
        TournamentEntity entity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        entity.setTeams(teams);
        tournamentRepository.save(entity);
    }

    // ...

    @Transactional
    public void updateReferee(String matchId, String referee) {
        MatchEntity matchEntity = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));

        matchEntity.setReferee(referee);
        matchRepository.save(matchEntity);
    }

    @Transactional
    public Optional<MatchEntity> updateMatchDate(String matchId, LocalDateTime date) {
        MatchEntity matchEntity = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        matchEntity.setMatchDate(date);

        matchRepository.save(matchEntity);
        return Optional.of(matchEntity);
    }

    @Transactional
    public Optional<MatchEntity> updateMatchStadium(String matchId, String stadium) {
        MatchEntity matchEntity = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        matchEntity.setStadium(stadium);


        matchRepository.save(matchEntity);
        return Optional.of(matchEntity);
    }

    public Optional<MatchEntity> updateMatchFreeSeats(String matchId, int freeSeats) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
        matchEntity.setFreeSeats(freeSeats);

        matchRepository.save(matchEntity);

        return Optional.of(matchEntity);
    }

    public void updateNextMatch(String matchId, String nextMatchId) {
        MatchEntity matchEntity = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));
        matchEntity.setNextMatchId(nextMatchId);
        matchRepository.save(matchEntity);
    }

    public void updateMatchTeams(String matchId, String teamA, String teamB) {
        MatchEntity match = getMatchOrThrow(matchId);
        match.setTeamA(teamA);
        match.setTeamB(teamB);
        matchRepository.save(match);
    }

    public void updateTournamentName(String tournamentId, String tournamentName) {
        TournamentEntity entity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        entity.setTournamentName(tournamentName);
        tournamentRepository.save(entity);
    }

    public void createTournament(String tournamentName, int maxTournamentTeams, List<String> teams) {
        TournamentEntity tournamentEntity = new TournamentEntity(tournamentName, maxTournamentTeams, teams);

        tournamentRepository.save(tournamentEntity);
    }

    public MatchEntity createMatch(String tournamentId, int stage, int matchIndex, String teamA, String teamB) {
        // null for the date, I assume it should be added later
        MatchEntity matchEntity = new MatchEntity(
                tournamentId, "", 0, "", null, stage, matchIndex, teamA, teamB, 0, 0, "", ""
        );
        return matchRepository.save(matchEntity);
    }

    public void saveMatch(MatchEntity match) {
        matchRepository.save(match);
    }

    public boolean existsByTournamentId(String tournamentId) {
        return tournamentRepository.existsById(tournamentId);
    }

    public boolean existsByMatchId(String matchId) {
        return matchRepository.existsById(matchId);
    }

    @Transactional
    public void deleteTournament(String tournamentId) {
        // Delete all matches associated with this tournament as they aren't connected with JPA
        List<MatchEntity> matches = matchRepository.findByTournamentId(tournamentId);
        matchRepository.deleteAll(matches);

        tournamentRepository.deleteById(tournamentId);
    }
}
