package org.example.amateurvolleyballtournament.users;

import java.util.List;

public record InputUserDTO(String id, String name, String surname, String email, String password, String role, List<String> team) {}
