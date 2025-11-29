package org.example.amateurvolleyballtournament.users;

import java.util.List;

public record UserDTO(String id, String name, String surname, String email, String role, List<String> team) {}
