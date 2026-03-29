package com.nexa.cda.authapp.auth.service;

import com.nexa.cda.authapp.auth.dto.LoginRequestDto;
import com.nexa.cda.authapp.auth.dto.LoginResponseDto;
import com.nexa.cda.authapp.auth.dto.RegisterRequestDto;
import com.nexa.cda.authapp.auth.dto.RegisterResponseDto;
import com.nexa.cda.authapp.auth.mapper.AuthDtoMapper;
import com.nexa.cda.authapp.common.exception.EmailAlreadyUsedException;
import com.nexa.cda.authapp.common.exception.InvalidCredentialsException;
import com.nexa.cda.authapp.security.JwtService;
import com.nexa.cda.authapp.user.dao.UserDao;
import com.nexa.cda.authapp.user.model.AppUser;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthDtoMapper authDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            AuthDtoMapper authDtoMapper,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.authDtoMapper = authDtoMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public RegisterResponseDto register(RegisterRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userDao.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        AppUser newUser = authDtoMapper.toNewUser(request, normalizedEmail, encodedPassword);
        AppUser savedUser;
        try {
            savedUser = userDao.save(newUser);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }
        return authDtoMapper.toRegisterResponse(savedUser);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }

        AppUser user = userDao.findByEmail(normalizedEmail).orElseThrow(InvalidCredentialsException::new);
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "username", user.getUsername())
        );

        return new LoginResponseDto(token, jwtService.getExpirationSeconds());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
