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
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

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
        String encryptedPwd = passwordEncoder.encode(user.password());

        //new Entity into the db
        UserEntity userEntity = new UserEntity(UUID.randomUUID().toString(),
                user.username(),
                user.surname(),
                encryptedPwd,
                user.email(),
                user.role(),
                true);
        //db save
        userRepository.save(userEntity);
    }

    @Override
    public void updateUser(String id, UserDTO user) {
        userRepository.findById(id).ifPresent(userEntity -> {
            userEntity.setUsername(user.username());
            userEntity.setSurname(user.surname());
            userEntity.setEmail(user.email());
            userEntity.setRole(user.role());
            userRepository.save(userEntity);//db update
            });
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
        //convert data from the db to a DTO
        return userRepository.findByRole(role).stream()
                .map(entity -> new UserDTO(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getSurname(),
                        entity.getEmail(),
                        entity.getRole()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deactivateUser(String id) {
        userRepository.findById(id).ifPresent(entity -> {
            entity.setActive(false);
            userRepository.save(entity);
        });
    }

    @Override
    public List<UserDTO> getAllUsers() {
        // Prende tutti dal DB e li converte in DTO
        return userRepository.findAll().stream()
                // Opzionale: filtrare quelli inattivi
                // .filter(UserEntity::isActive)
                .map(entity -> new UserDTO(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getSurname(),
                        entity.getEmail(),
                        entity.getRole()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> exportUsersByRole(String role) {
        return listUsersByRole(role);
    }

    @Override
    public Boolean isCoach(String userId) {
        return userRepository.findById(userId)
                .map(u -> u.isActive() && "COACH".equalsIgnoreCase(u.getRole()))
                .orElse(false);
    }

    @Override
    public boolean isAdmin(String userId) {
        return userRepository.findById(userId)
                .map(u -> u.isActive() && "ADMIN".equalsIgnoreCase(u.getRole()))
                .orElse(false);
    }

    @Override
    public String getCoach(String id) throws Exception {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        if ("COACH".equalsIgnoreCase(user.getRole())) {
            return user.getUsername() + " " + user.getSurname();
        } else {
            throw new Exception("Unauthorized: User is not a coach");
        }
    }
}

