package org.example.amateurvolleyballtournament.users.internal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
        if (userRepository.findByEmail(user.email()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.email() + " already exists.");
        }

        String encryptedPwd = passwordEncoder.encode(user.password());

        String userId = (user.id() != null && !user.id().isEmpty())
                ? user.id()
                : UUID.randomUUID().toString();

        //new Entity into the db
        UserEntity userEntity = new UserEntity(userId,
                user.username(),
                user.surname(),
                user.email(),
                encryptedPwd,
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
            // 1. Generate token
            String token = jwtService.generateToken(user.getEmail(), user.getRole());

            // 2. Prepare user data
            UserDTO userDto = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getSurname(),
                    user.getEmail(),
                    user.getRole()
            );

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
        // Fetches all the users from DB and convert them to DTO
        return userRepository.findAll().stream()
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

    @Override
    public String extractEmailFromToken(String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            return null;
        }

        // Remove Bearer if present
        if (jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            // Same logic as JwtService
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtService.getSecretKey().getBytes()) // Key from JwtService
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String extractRoleFromToken(String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            return null;
        }

        // Remove Bearer if present
        if (jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtService.getSecretKey().getBytes())
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isAdminFromToken(String jwtToken) {
        String role = extractRoleFromToken(jwtToken);
        return "ADMIN".equalsIgnoreCase(role);
    }
}

