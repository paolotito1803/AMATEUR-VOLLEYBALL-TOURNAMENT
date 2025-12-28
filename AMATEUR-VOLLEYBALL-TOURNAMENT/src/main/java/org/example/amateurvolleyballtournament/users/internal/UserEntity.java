package org.example.amateurvolleyballtournament.users.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; // Consiglio Lombok per ridurre boilerplate, altrimenti genera getter/setter
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", schema = "users_module")
@Data
@AllArgsConstructor
@NoArgsConstructor // Crea il costruttore vuoto
public class UserEntity {
    @Id
    private String id; //UUID

    @Column(unique = false, nullable = false)
    private String username;

    @Column(unique = false, nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = false, nullable = false)
    private String role; //COACH, PLAYER, REFEREE, VIEWER, ADMIN

    @Column(name = "is_active")
    private boolean isActive = true;

}
