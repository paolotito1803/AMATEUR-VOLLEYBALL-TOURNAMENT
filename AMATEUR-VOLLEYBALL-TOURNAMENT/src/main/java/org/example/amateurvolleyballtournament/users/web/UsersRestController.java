package org.example.amateurvolleyballtournament.users.web;

import org.example.amateurvolleyballtournament.users.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
class UsersRestController {

    private final UsersService usersService;

    UsersRestController(UsersService usersService) {
        this.usersService = usersService;
    }

    // GET all users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return usersService.getAllUsers();
    }

    // POST create user
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody InputUserDTO user) {
        try {
            usersService.createUser(user);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO user) {
        //verify the tolen validity
        try {
            LoginDTO response = usersService.login(user.email(), user.password());
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // DELETE reset all users (dev tool)
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetUsers() {
        usersService.getAllUsers().forEach(u -> usersService.deactivateUser(u.id()));
        return ResponseEntity.ok("All users deactivated.");
    }

    // GET export users by role
    @GetMapping("/export/{role}")
    public List<UserDTO> exportByRole(@PathVariable String role) {
        return usersService.exportUsersByRole(role);
    }
}
