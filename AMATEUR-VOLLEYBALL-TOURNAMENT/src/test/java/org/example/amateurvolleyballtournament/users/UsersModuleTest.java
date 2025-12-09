package org.example.amateurvolleyballtournament.users;

import jakarta.transaction.Transactional;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.example.amateurvolleyballtournament.users.internal.UserEntity;
import org.example.amateurvolleyballtournament.users.internal.UserRepository;
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
@Transactional
public class UsersModuleTest {
    @Autowired
    UsersService usersService;

    @Autowired
    UserRepository userRepository; //verify db state

    @Test
    void shouldCreateUserSuccesfully() {
       String new_id = String.valueOf(UUID.randomUUID());
        InputUserDTO users = new InputUserDTO(
                new_id, "John", "Doe","password123", "john@mail.com", "COACH"
        );
        usersService.createUser(users);
        assertThat(usersService.getAllUsers())
                .anyMatch(u -> u.username().equals("John") && u.surname().equals("Doe"));
    }

    @Test
    void shouldLoginSuccesfully() throws Exception{
        InputUserDTO users = new InputUserDTO(null, "Loginuser", "Test", "mySecretPWD", "logintest@mail.com", "PLAYER");
        usersService.createUser(users);

        //login test
        LoginDTO response = usersService.login("logintest@mail.com", "mySecretPWD");

        assertThat(response.token()).isNotBlank(); //token must be exists
        assertThat(response.user().email()).isEqualTo("logintest@mail.com");
    }

    @Test
    void shouldLoginFailureWithWrongInput(){
        InputUserDTO users = new InputUserDTO(null, "Hacker", "Test", "correctPwd", "hacker@test.com", "PLAYER");
        usersService.createUser(users);

        assertThatThrownBy(() -> usersService.login("hacker@test.com", "WRONG_PWD")).isInstanceOf(Exception.class).hasMessageContaining("Invalid credentials");
    }

    @Test
    void shouldDeactiveUsers(){
        usersService.createUser(new InputUserDTO(
                UUID.randomUUID().toString(), "Users", "TMP","pwd", "t@mail.com", "VIEWER"
        ));

        String userId = usersService.getAllUsers().stream().filter(u -> u.email().equals("t@mail.com")).findFirst().orElseThrow().id();
        usersService.deactivateUser(userId);

        assertThat(usersService.getAllUsers()).anyMatch(u -> u.id().equals(userId) && u.role().equals(userId));
        UserEntity entity = userRepository.findById(userId).orElseThrow();
        assertThat(entity.isActive()).isFalse();
        assertThat(entity.getRole()).isEqualTo("VIEWER");
    }

    @Test
    void shouldExportUsersByRole(){
        usersService.createUser(new InputUserDTO(UUID.randomUUID().toString(), "Users", "TMP","pwd", "t@mail.com", "REFEREE"));
        var exported = usersService.exportUsersByRole("REFEREE");
        assertThat(exported).hasSize(1);
        assertThat(exported).allMatch(u -> "REFEREE".equalsIgnoreCase(u.role()));
    }
}
