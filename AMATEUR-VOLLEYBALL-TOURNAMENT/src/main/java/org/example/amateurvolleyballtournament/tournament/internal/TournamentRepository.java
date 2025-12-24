package org.example.amateurvolleyballtournament.tournament.internal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
interface TournamentRepository extends JpaRepository<TournamentEntity, String> {

    Optional<TournamentEntity> findByTournamentName(String tournamentName);

    boolean existsById(String tournamentId);
}
