package com.nexa.cda.authapp.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexa.cda.authapp.auth.mapper.AuthViewMapper;
import com.nexa.cda.authapp.auth.view.LoginRequest;
import com.nexa.cda.authapp.auth.view.LoginResponse;
import com.nexa.cda.authapp.auth.view.RegisterRequest;
import com.nexa.cda.authapp.auth.view.RegisterResponse;
import com.nexa.cda.authapp.common.exception.EmailAlreadyUsedException;
import com.nexa.cda.authapp.common.exception.InvalidCredentialsException;
import com.nexa.cda.authapp.security.JwtService;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.model.UserRole;
import com.nexa.cda.authapp.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthViewMapper authViewMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, authViewMapper, authenticationManager, jwtService);
    }

    @Test
    void registerShouldPersistMappedUserAndReturnViewResponse() {
        RegisterRequest request = new RegisterRequest("Nexa", "NEXA@EXAMPLE.COM", "StrongPass123");
        AppUser mapped = user(1L, "Nexa", "nexa@example.com", "hash", UserRole.USER);
        RegisterResponse expected = new RegisterResponse(
                1L,
                "Nexa",
                "nexa@example.com",
                "USER",
                Instant.parse("2026-03-29T10:00:00Z")
        );

        when(userRepository.existsByEmail("nexa@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass123")).thenReturn("hash");
        when(authViewMapper.toNewUser(request, "nexa@example.com", "hash")).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(mapped);
        when(authViewMapper.toRegisterResponse(mapped)).thenReturn(expected);

        RegisterResponse response = authService.register(request);

        assertEquals(expected, response);
        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(userRepository).save(mapped);
    }

    @Test
    void registerShouldFailWhenEmailAlreadyUsed() {
        RegisterRequest request = new RegisterRequest("Nexa", "nexa@example.com", "StrongPass123");
        when(userRepository.existsByEmail("nexa@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("nexa@example.com", "StrongPass123");
        AppUser user = user(1L, "Nexa", "nexa@example.com", "hash", UserRole.USER);

        when(userRepository.findByEmail("nexa@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("nexa@example.com", "StrongPass123"));
        assertEquals("jwt-token", response.token());
        assertEquals(3600L, response.expiresInSeconds());
    }

    @Test
    void loginShouldFailWhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("nexa@example.com", "wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    private static AppUser user(Long id, String username, String email, String password, UserRole role) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setCreatedAt(Instant.parse("2026-03-29T10:00:00Z"));

        try {
            var field = AppUser.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

        return user;
    }
}
