package org.example.amateurvolleyballtournament.tournament.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {
    // TODO: non so se può essere utile. Nel caso va fatto un refactoring di Entity e DTO per inserirlo
    // List<MatchEntity> findByTournamentId(String tournamentId);
    Optional<MatchEntity> findById(String matchId);

    boolean existsById(String matchId);
    @Modifying
    @Query("""
        UPDATE MatchEntity m
        SET m.freeSeats = m.freeSeats - 1
        WHERE m.matchId = :matchId AND m.freeSeats > 0
    """)
    int decrementFreeSeatsIfAvailable(String matchId);
//    Optional<MatchEntity> findbyStadiumName(String stadiumName);
//
//    Optional<MatchEntity> findbyMatchDate(Date matchDate);
//
//    Optional<MatchEntity> findbyMatchId(int matchId);
//
//    Optional<MatchEntity> findbyMatchReferee(String referee);
//
//    boolean existsByStadiumName(String stadiumName);
//    boolean existsByMatchDate(Date matchDate);
//    boolean existsByMatchId(int matchId);
//    boolean existsByMatchReferee(String referee);
}