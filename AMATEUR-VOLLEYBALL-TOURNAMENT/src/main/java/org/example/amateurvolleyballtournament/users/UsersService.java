package org.example.amateurvolleyballtournament.users;

import java.util.List;

public interface UsersService {
    void registerUsers(UserDTO users);

    void updateUsers(String id, UserDTO users);

    void deleteUsers(String id);

    List<UserDTO> getUsersByRole(String role);

    List<UserDTO> exportUsers(String Role);

    void assignTeamToUsers(String userId, String teamName);
}
