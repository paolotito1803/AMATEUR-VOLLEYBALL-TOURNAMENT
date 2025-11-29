package org.example.amateurvolleyballtournament.users.internal;

import org.example.amateurvolleyballtournament.users.InputUserDTO;
import org.example.amateurvolleyballtournament.users.UserDTO;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {
    @Override
    public void createUser(InputUserDTO user) {

    }

    @Override
    public void updateUser(String id, UserDTO user) {

    }

    @Override
    public List<UserDTO> listUsersByRole(String role) {
        return List.of();
    }

    @Override
    public void assignTeamsToUser(String userId, String teamName) {

    }

    @Override
    public void deactivateUser(String id) {

    }

    @Override
    public List<UserDTO> getAllUsers() {
        return List.of();
    }

    @Override
    public List<UserDTO> exportUsersByRole(String role) {
        return List.of();
    }
}
