package org.example.amateurvolleyballtournament.users;

import org.example.amateurvolleyballtournament.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.ApplicationModule;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApplicationModuleTest
public class UsersModuleTest {
    @Autowired
    UsersService usersService;

    @Test
    void shouldCreateUserSuccesfully() {
       String new_id = String.valueOf(UUID.randomUUID());
        InputUserDTO users = new InputUserDTO(
                new_id, "John", "Doe","password123", "john@mail.com", "COACH", ""
        );
        usersService.createUser(users);
        assertThat(usersService.getAllUsers())
                .anyMatch(u -> u.username().equals("John") && u.surname().equals("Doe"));
    }

    @Test
    void shouldDeactiveUsers(){
        usersService.createUser(new InputUserDTO(
                UUID.randomUUID().toString(), "Users", "TMP","pwd", "t@mail.com", "VIEWER", ""
        ));

        String userId = usersService.getAllUsers().stream().findFirst().orElseThrow(() -> new AssertionError("No users found"))
                .id();
        usersService.deactivateUser(userId);

        assertThat(usersService.getAllUsers())
                .anyMatch(u ->
                        u.id().equals(userId)
                                && u.role().equals("INACTIVE")
                );
    }

    @Test
    void shouldExportUsersByRole(){
        usersService.createUser(new InputUserDTO(UUID.randomUUID().toString(), "Users", "TMP","pwd", "t@mail.com", "REFEREE", ""));
        var exported = usersService.exportUsersByRole("REFEREE");
        assertThat(exported).hasSize(1);
        assertThat(exported).allMatch(u -> "REFEREE".equalsIgnoreCase(u.role()));
    }
}
