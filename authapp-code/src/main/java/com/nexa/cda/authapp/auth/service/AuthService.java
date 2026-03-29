package com.nexa.cda.authapp.auth.service;

import com.nexa.cda.authapp.auth.mapper.AuthViewMapper;
import com.nexa.cda.authapp.auth.view.LoginRequest;
import com.nexa.cda.authapp.auth.view.LoginResponse;
import com.nexa.cda.authapp.auth.view.RegisterRequest;
import com.nexa.cda.authapp.auth.view.RegisterResponse;
import com.nexa.cda.authapp.common.exception.EmailAlreadyUsedException;
import com.nexa.cda.authapp.common.exception.InvalidCredentialsException;
import com.nexa.cda.authapp.security.JwtService;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.repository.UserRepository;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthViewMapper authViewMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthViewMapper authViewMapper,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authViewMapper = authViewMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        AppUser newUser = authViewMapper.toNewUser(request, normalizedEmail, encodedPassword);
        AppUser savedUser;
        try {
            savedUser = userRepository.save(newUser);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }
        return authViewMapper.toRegisterResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }

        AppUser user = userRepository.findByEmail(normalizedEmail).orElseThrow(InvalidCredentialsException::new);
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "username", user.getUsername())
        );

        return new LoginResponse(token, jwtService.getExpirationSeconds());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
