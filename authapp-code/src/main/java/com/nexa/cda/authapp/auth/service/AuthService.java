package com.nexa.cda.authapp.auth.service;

import com.nexa.cda.authapp.auth.mapper.AuthViewMapper;
import com.nexa.cda.authapp.auth.view.RegisterRequest;
import com.nexa.cda.authapp.auth.view.RegisterResponse;
import com.nexa.cda.authapp.common.exception.EmailAlreadyUsedException;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthViewMapper authViewMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthViewMapper authViewMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authViewMapper = authViewMapper;
    }

    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        AppUser newUser = authViewMapper.toNewUser(request, normalizedEmail, encodedPassword);
        AppUser savedUser = userRepository.save(newUser);
        return authViewMapper.toRegisterResponse(savedUser);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
