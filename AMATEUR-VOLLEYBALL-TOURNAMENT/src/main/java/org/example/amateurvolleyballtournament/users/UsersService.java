package org.example.amateurvolleyballtournament.users;

public interface UsersService {
    boolean isCoach(String userId);
    boolean isAdmin(String userId);
}
