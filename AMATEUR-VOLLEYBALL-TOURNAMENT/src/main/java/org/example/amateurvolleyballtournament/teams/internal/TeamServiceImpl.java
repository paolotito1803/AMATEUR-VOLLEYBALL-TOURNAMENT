package org.example.amateurvolleyballtournament.teams.internal;

import org.example.amateurvolleyballtournament.teams.PlayerDTO;
import org.example.amateurvolleyballtournament.teams.StadiumDTO;
import org.example.amateurvolleyballtournament.teams.TeamDTO;
import org.example.amateurvolleyballtournament.teams.TeamService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class TeamServiceImpl implements TeamService {

    private final TeamPersistence db;
    private final UsersService usersService;

    TeamServiceImpl(TeamPersistence db, UsersService usersService) {
        this.db = db;
        this.usersService = usersService;
    }

    @Override
    public void registerTeam(String coachId, String coachName, String teamName) {
        // 1. Validate User Role (External Module call)
        // TODO: re enable after testing
        //if (!usersService.isCoach(coachId)) {
            //throw new IllegalArgumentException("User " + coachId + " is not authorized to register a team (Not a Coach).");
        //}

        // 2. Validate Coach Uniqueness (Rule: 1 Team per Coach)
        if (db.existsByCoachId(coachId)) {
            throw new IllegalStateException("Coach " + coachName + " already has a registered team.");
        }

        // 3. Validate Team Name Uniqueness
        if (db.existsByName(teamName)) {
            throw new IllegalArgumentException("Team name '" + teamName + "' is already taken.");
        }

        // 4. Create and Save
        // The Constructor of TeamEntity generates the UUID and initializes empty lists
        TeamEntity newTeam = new TeamEntity(coachId, coachName, teamName);

        // 5. Save
        db.save(newTeam);
    }

    @Override
    public void changeCoach(String oldCoachId, String newCoachId, String newCoachName) {
        // 1. Validate the new coach exists and is actually a coach
        // TODO: re enable after testing
        //if (!usersService.isCoach(newCoachId)) {
            //throw new IllegalArgumentException("New user " + newCoachId + " is not a valid Coach.");
        //}

        // 2. Ensure the new coach doesn't already have a team
        if (db.existsByCoachId(newCoachId)) {
            throw new IllegalStateException("The new coach already leads another team. Cannot transfer ownership.");
        }

        // 3. Get the team belonging to the old coach (throws exception if not found)
        TeamEntity team = db.getByCoachIdOrThrow(oldCoachId);

        // 4. Update details
        team.setCoachId(newCoachId);
        team.setCoachName(newCoachName);

        // 5. Save
        db.save(team);
    }

    @Override
    public void updateStadium(String coachId, StadiumDTO stadium) {
        // 1. Validate Inputs
        if (stadium == null || stadium.name() == null || stadium.name().isBlank()) {
            throw new IllegalArgumentException("Stadium name cannot be empty.");
        }
        if (stadium.maxSeats() <= 0) {
            throw new IllegalArgumentException("Stadium capacity must be greater than 0.");
        }

        // 2. Get the team (Implicitly checks rights: if coachId has no team, this throws)
        TeamEntity team = db.getByCoachIdOrThrow(coachId);

        // 3. Update the state
        // Since StadiumEmbeddable is just data, we create a new one.
        StadiumEmbeddable newStadium = new StadiumEmbeddable(stadium.name(), stadium.maxSeats());
        team.setStadium(newStadium);

        // 4. Save
        db.save(team);
    }

    @Override
    public void addPlayer(String coachId, PlayerDTO player) {
        // 1. Validate Inputs
        if (player == null) {
            throw new IllegalArgumentException("Player data cannot be null.");
        }

        // 2. Retrieve Team
        TeamEntity team = db.getByCoachIdOrThrow(coachId);

        // 3. Business Rule: Check for duplicate jersey numbers
        boolean numberAlreadyTaken = team.getPlayers().stream()
                .anyMatch(p -> p.getNumber() == player.number());

        if (numberAlreadyTaken) {
            throw new IllegalArgumentException("Player number " + player.number() + " is already taken in this team.");
        }

        // 4. Create and Link Entity
        // The ID is auto-generated
        PlayerEntity newPlayer = new PlayerEntity(
                player.name(),
                player.number(),
                player.role(),
                team
        );

        // 5. Add to list and Save
        team.addPlayer(newPlayer);
        db.save(team);
    }

    @Override
    public void updatePlayer(String coachId, int currentNumber, PlayerDTO updatedDetails) {
        // 1. Retrieve Team
        TeamEntity team = db.getByCoachIdOrThrow(coachId);

        // 2. Find the player to update
        PlayerEntity playerToUpdate = team.getPlayers().stream()
                .filter(p -> p.getNumber() == currentNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player with number " + currentNumber + " not found in this team."));

        // 3. Business Rule: Logic for changing jersey numbers
        if (updatedDetails.number() != currentNumber) {
            // Check if the new number is already taken by someone else
            boolean numberTaken = team.getPlayers().stream()
                    .anyMatch(p -> p.getNumber() == updatedDetails.number());

            if (numberTaken) {
                throw new IllegalArgumentException("Cannot change number to " + updatedDetails.number() + " because it is already taken.");
            }
        }

        // 4. Update Fields
        playerToUpdate.setName(updatedDetails.name());
        playerToUpdate.setRole(updatedDetails.role());
        playerToUpdate.setNumber(updatedDetails.number());

        // 5. Save
        db.save(team);
    }

    @Override
    public void removePlayer(String coachId, int playerNumber) {
        // Because 'orphanRemoval = true' is set in TeamEntity, removing it from the list
        // and saving the team triggers a DELETE SQL statement.
        db.deletePlayer(coachId, playerNumber);
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        // 1. Get all entities from persistence
        // 2. Stream through them
        // 3. Convert each to DTO
        List<TeamEntity> entities = (List<TeamEntity>) db.findAll();
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<StadiumDTO> getStadiumByTeamId(String teamId) {
        // Look up the team, then extract just the stadium part
        return db.findById(teamId)
                .map(TeamEntity::getStadium)
                .map(s -> new StadiumDTO(s.getStadiumName(), s.getStadiumCapacity()));
    }

    @Override
    public Optional<StadiumDTO> getStadiumByName(String name) {
        // Find any team that plays in this stadium to get the capacity details.
        return db.findByStadiumName(name)
                .map(TeamEntity::getStadium)
                .map(s -> new StadiumDTO(s.getStadiumName(), s.getStadiumCapacity()));
    }

    @Override
    public Optional<TeamDTO> getTeam(String teamId) {
        return db.findById(teamId)
                .map(this::toDTO);
    }

    @Override
    public void deleteAllTeams() {
        db.deleteAll();
    }

    // Converts Internal Entity -> Public DTO
    private TeamDTO toDTO(TeamEntity entity) {
        // Handle Null Stadium safely
        StadiumDTO stadiumDTO = null;
        if (entity.getStadium() != null) {
            stadiumDTO = new StadiumDTO(
                    entity.getStadium().getStadiumName(),
                    entity.getStadium().getStadiumCapacity()
            );
        }

        // Convert List<PlayerEntity> to List<PlayerDTO>
        List<PlayerDTO> playerDTOs = entity.getPlayers().stream()
                .map(p -> new PlayerDTO(p.getName(), p.getNumber(), p.getRole()))
                .toList();

        return new TeamDTO(
                entity.getId(),
                entity.getCoachId(),
                entity.getName(),
                entity.getCoachName(),
                playerDTOs,
                stadiumDTO
        );
    }
}
