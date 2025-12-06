package org.example.amateurvolleyballtournament.users;

public record InputUserDTO(String id,
                           String username,
                           String surname,
                           String password,
                           String email,
                           String role) {}

