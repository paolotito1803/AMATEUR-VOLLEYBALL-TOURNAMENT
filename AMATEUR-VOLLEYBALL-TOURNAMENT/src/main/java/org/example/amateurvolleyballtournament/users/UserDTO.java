package org.example.amateurvolleyballtournament.users;

public record UserDTO(String id,
                      String username,
                      String surname,
                      String fullName,
                      String email,
                      String role,
                      String teamName) {}
