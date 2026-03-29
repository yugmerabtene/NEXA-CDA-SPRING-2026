package com.nexa.cda.authapp.user.service;

import com.nexa.cda.authapp.common.exception.UserNotFoundException;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.repository.UserRepository;
import com.nexa.cda.authapp.user.view.MeResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MeResponse getCurrentUser(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return new MeResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
