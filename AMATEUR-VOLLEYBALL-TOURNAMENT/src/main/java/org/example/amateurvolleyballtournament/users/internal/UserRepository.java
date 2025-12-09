package org.example.amateurvolleyballtournament.users.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    // Select * from users where email = ?
    Optional<UserEntity> findByEmail(String email);

    // Select * from users where role = ?
    List<UserEntity> findByRole(String role);
}
