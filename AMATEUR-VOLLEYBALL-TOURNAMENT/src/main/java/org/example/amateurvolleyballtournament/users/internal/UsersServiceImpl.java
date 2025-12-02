package org.example.amateurvolleyballtournament.users.internal;

import org.example.amateurvolleyballtournament.users.InputUserDTO;
import org.example.amateurvolleyballtournament.users.UserDTO;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {
    private final ArrayList<UserDTO> users = new ArrayList<>();
    private final ArrayList<String> pwd = new ArrayList<>();


    @Override
    public void createUser(InputUserDTO user) {
        String id = UUID.randomUUID().toString();

        UserDTO newuser = new UserDTO(id, user.surname(), user.surname(), user.email(), user.role(), user.teamName());
        users.add(newuser);

        pwd.add(user.password()); //password saved in clear
    }

    @Override
    public void updateUser(String id, UserDTO user) {
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).id().equals(id)) {
                users.set(i, user);
                return;
            }
        }
    }

    @Override
    public List<UserDTO> listUsersByRole(String role) {
        List<UserDTO> roles = new ArrayList<>();
        for(UserDTO user: users){
            if(user.role().equals(role)) {
                roles.add(user);
            }
        }
        return roles;
    }

    @Override
    public void assignTeamsToUser(String userId, String teamName) {
        //chiedere se da fare
    }

    @Override
    public void deactivateUser(String id) {
        for(int i = 0; i < users.size(); i++) {
            UserDTO user = users.get(i);
            if(user.id().equals(id)) {
                users.set(i, new UserDTO(user.id(), user.username(), user.surname(), user.email(), "Inactive", user.teamName()));
            }
        }
        return;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return users;
    }

    @Override
    public List<UserDTO> exportUsersByRole(String role) {
        return listUsersByRole(role);
    }
}