package org.example.amateurvolleyballtournament.tournament.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {

    List<MatchEntity> findByTournamentId(String tournamentId);

    List<MatchEntity> findByTournamentIdAndStage(String tournamentId, int stage);

    // Find the highest stage number for when tournament ends
    @Query("SELECT COALESCE(MAX(m.stage), 0) FROM MatchEntity m WHERE m.tournamentId = :tournamentId")
    int findMaxStageByTournamentId(String tournamentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE MatchEntity m
        SET m.freeSeats = m.freeSeats - 1
        WHERE m.matchId = :matchId AND m.freeSeats > 0
    """)
    int decrementFreeSeatsIfAvailable(String matchId);
}