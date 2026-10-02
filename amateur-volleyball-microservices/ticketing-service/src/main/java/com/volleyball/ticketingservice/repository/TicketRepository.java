package com.volleyball.ticketingservice.repository;

import com.volleyball.ticketingservice.entities.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, String> {
    List<TicketEntity> findByUserId(String userId);

    boolean existsByMatchIdAndNumSeat(String matchId, int numSeat);

    void deleteByMatchId(String matchId);

    @Query("SELECT t.numSeat FROM TicketEntity t WHERE t.matchId = :matchId")
    List<Integer> findTakenSeatsByMatchId(String matchId);
}
