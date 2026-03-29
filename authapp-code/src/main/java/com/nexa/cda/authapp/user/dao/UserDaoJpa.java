package com.nexa.cda.authapp.user.dao;

import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserDaoJpa implements UserDao {

    private final UserRepository userRepository;

    public UserDaoJpa(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public AppUser save(AppUser user) {
        return userRepository.save(user);
    }
}
