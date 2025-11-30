package org.example.amateurvolleyballtournament.users.internal;

import org.example.amateurvolleyballtournament.users.UsersService;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {
    @Override
    public boolean isCoach(String userId) {
        return true;
    }
}
