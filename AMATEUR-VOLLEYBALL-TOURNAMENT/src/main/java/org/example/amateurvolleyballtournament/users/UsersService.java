package org.example.amateurvolleyballtournament.users;

import java.util.List;

public interface UsersService {
    // Creazione nuovo utente
    void createUser(InputUserDTO user);

    // Aggiornamento utente esistente
    void updateUser(String id, UserDTO user);


    // Lista utenti filtrata per ruolo
    List<UserDTO> listUsersByRole(String role);

    // Assegna team a un utente (coach/referee)
    void assignTeamsToUser(String userId,String teamName);

    // Disattiva utente (flag)
    void deactivateUser(String id);

    // Lista tutti gli utenti
    List<UserDTO> getAllUsers();

    // Export utenti filtrati per ruolo (può essere JSON, CSV o lista)
    List<UserDTO> exportUsersByRole(String role);

    Boolean isCoach(String uniqueCoachId);

    boolean isAdmin(String userId);
}
