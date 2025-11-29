package org.example.amateurvolleyballtournament.users.internal;

import org.example.amateurvolleyballtournament.users.UserDTO;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {
    @Override
    public void registerUsers(UserDTO users) {

    }

    @Override
    public void updateUsers(String id, UserDTO users) {

    }

    @Override
    public void deleteUsers(String id) {

    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        return List.of();
    }

    @Override
    public List<UserDTO> exportUsers(String Role) {
        return List.of();
    }

    @Override
    public void assignTeamToUsers(String userId, String teamName) {

    }
}
