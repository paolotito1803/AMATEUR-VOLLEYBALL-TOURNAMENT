package org.example.amateurvolleyballtournament.users.internal;

import org.example.amateurvolleyballtournament.users.InputUserDTO;
import org.example.amateurvolleyballtournament.users.LoginDTO;
import org.example.amateurvolleyballtournament.users.UserDTO;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    private final ArrayList<UserDTO> users = new ArrayList<>();
    private final ArrayList<String> pwd = new ArrayList<>();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsersServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void createUser(InputUserDTO user) {
        String id = UUID.randomUUID().toString();

        UserDTO newuser = new UserDTO(id, user.surname(), user.surname(), user.email(), user.role());
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
    public LoginDTO login(String email, String password) throws Exception {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("Invalid credentials"));

        if (!user.isActive()) {
            throw new Exception("User is inactive");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            // 1. Generiamo il Token
            String token = jwtService.generateToken(user.getEmail(), user.getRole());

            // 2. Prepariamo i dati utente
            UserDTO userDto = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getSurname(),
                    user.getEmail(),
                    user.getRole()
            );

            // 3. Ritorniamo entrambi (Token + Dati)
            return new LoginDTO(token, userDto);

        } else {
            throw new Exception("Invalid credentials");
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
    public void deactivateUser(String id) {
        for(int i = 0; i < users.size(); i++) {
            UserDTO user = users.get(i);
            if(user.id().equals(id)) {
                users.set(i, new UserDTO(user.id(), user.username(), user.surname(), user.email(), "Inactive"));
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

    @Override
    public Boolean isCoach(String userId) {
        return users.stream()
                .anyMatch(u ->
                        u.id().equals(userId) &&
                                u.role().equalsIgnoreCase("COACH")
                );
    }

    @Override
    public boolean isAdmin(String userId) {
        return users.stream()
                .anyMatch(u ->
                        u.id().equals(userId) &&
                                u.role().equalsIgnoreCase("ADMIN")
                );
    }

    @Override
    public String getCoach(String id) throws Exception {
        String name = "";
        String surname = "";
        String coach_name = "";
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).id().equals(id) && users.get(i).role().equals("COACH")) {
                name = users.get(i).username();
                surname = users.get(i).surname();
                coach_name = name + " " + surname;
            }
            else{
                throw  new Exception("Unauthorized coach");
            }
        }
        return coach_name;
    }
}

