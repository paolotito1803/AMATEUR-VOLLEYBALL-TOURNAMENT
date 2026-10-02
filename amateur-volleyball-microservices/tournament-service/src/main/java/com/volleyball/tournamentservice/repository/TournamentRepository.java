package com.volleyball.tournamentservice.repository;

import com.volleyball.tournamentservice.entities.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
    Optional<TournamentEntity> findByTournamentName(String tournamentName);
}
