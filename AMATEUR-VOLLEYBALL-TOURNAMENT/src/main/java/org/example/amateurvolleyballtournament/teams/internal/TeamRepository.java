package org.example.amateurvolleyballtournament.teams.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// <TeamEntity, String> means: Managing 'TeamEntity' where the ID is 'String'
@Repository
interface TeamRepository extends JpaRepository<TeamEntity, String> {

    // SELECT * FROM teams WHERE coach_id = ?
    Optional<TeamEntity> findByCoachId(String coachId);

    // SELECT * FROM teams WHERE stadium_name = ?
    Optional<TeamEntity> findByStadium_StadiumName(String stadiumName);

    boolean existsByCoachId(String coachId);
    boolean existsByName(String name);
}
