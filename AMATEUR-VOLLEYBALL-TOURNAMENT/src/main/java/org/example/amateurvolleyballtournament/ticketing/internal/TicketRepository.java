package org.example.amateurvolleyballtournament.ticketing.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, String> {
    // SELECT * FROM tickets WHERE user_id = ?
    List<TicketEntity> findByUserId(String userId);

    // SELECT * FROM tickets WHERE match_id = ?
    List<TicketEntity> findByMatch(String match);

    // Check if a specific seat is already taken in a specific match
    boolean existsByMatchAndNumSeat(String match, int numSeat);
}
