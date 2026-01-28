package org.example.amateurvolleyballtournament.users;

import java.util.List;

public interface UsersService {
    // New user creation
    void createUser(InputUserDTO user);

    // Updates existing user
    void updateUser(String id, UserDTO user);

    //login
    LoginDTO login(String email, String password) throws Exception;


    // Filters user list by role
    List<UserDTO> listUsersByRole(String role);

    // Deactivate user
    void deactivateUser(String id);

    // List with all the users
    List<UserDTO> getAllUsers();

    // Exports filtered user list by role (JSON, CSV o list)
    List<UserDTO> exportUsersByRole(String role);
    
    Boolean isCoach(String uniqueCoachId);

    boolean isAdmin(String userId);
    
    //Returns coach name and lastname
    String getCoach(String id) throws Exception;

    String extractEmailFromToken(String jwtToken);

    String extractRoleFromToken(String jwtToken);

    boolean isAdminFromToken(String jwtToken);
}
