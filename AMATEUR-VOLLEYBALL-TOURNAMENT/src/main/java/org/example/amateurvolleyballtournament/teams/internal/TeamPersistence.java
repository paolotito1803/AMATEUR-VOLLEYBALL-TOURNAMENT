package org.example.amateurvolleyballtournament.teams.internal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class TeamPersistence {
    private final TeamRepository repo;

    TeamPersistence(TeamRepository repo) {
        this.repo = repo;
    }

    // WRITES (Transactional)

    @Transactional
    public void save(TeamEntity team) {
        repo.save(team);
    }

    @Transactional
    public void deleteAll() {
        repo.deleteAll(); // JpaRepository method: DELETE FROM teams;
    }

    @Transactional
    public void deletePlayer(String coachId, int playerNumber) {
        TeamEntity team = getByCoachIdOrThrow(coachId);

        // We try to remove.
        // removeIf returns 'true' if any elements were removed.
        boolean changed = team.getPlayers().removeIf(p -> p.getNumber() == playerNumber);

        // Only save to DB if something actually changed.
        // If the player wasn't there, we do nothing and return successfully.
        if (changed) {
            repo.save(team);
        }
    }

    // READS

    public boolean existsByCoachId(String coachId) {
        return repo.existsByCoachId(coachId);
    }

    public boolean existsByName(String teamName) {
        return repo.existsByName(teamName);
    }

    public TeamEntity getByCoachIdOrThrow(String coachId) {
        return repo.findByCoachId(coachId)
                .orElseThrow(() -> new RuntimeException("Team not found for coach: " + coachId));
    }

    public Optional<TeamEntity> findById(String id) {
        return repo.findById(id);
    }

    public Iterable<TeamEntity> findAll() {
        return repo.findAll();
    }

    public Optional<TeamEntity> findByStadiumName(String name) {
        return repo.findByStadium_StadiumName(name);
    }
}
