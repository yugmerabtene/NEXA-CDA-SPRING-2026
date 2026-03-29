package com.nexa.cda.authapp.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.nexa.cda.authapp.user.model.UserRole;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthDtoMapper authDtoMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDao, passwordEncoder, authDtoMapper, authenticationManager, jwtService);
    }

    @Test
    void registerShouldPersistMappedUserAndReturnViewResponse() {
        RegisterRequestDto request = new RegisterRequestDto("Nexa", "NEXA@EXAMPLE.COM", "StrongPass123");
        AppUser mapped = user(1L, "Nexa", "nexa@example.com", "hash", UserRole.USER);
        RegisterResponseDto expected = new RegisterResponseDto(
                1L,
                "Nexa",
                "nexa@example.com",
                "USER",
                Instant.parse("2026-03-29T10:00:00Z")
        );

        when(userDao.existsByEmail("nexa@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass123")).thenReturn("hash");
        when(authDtoMapper.toNewUser(request, "nexa@example.com", "hash")).thenReturn(mapped);
        when(userDao.save(mapped)).thenReturn(mapped);
        when(authDtoMapper.toRegisterResponse(mapped)).thenReturn(expected);

        RegisterResponseDto response = authService.register(request);

        assertEquals(expected, response);
        verify(userDao).save(mapped);
    }

    @Test
    void registerShouldFailWhenEmailAlreadyUsed() {
        RegisterRequestDto request = new RegisterRequestDto("Nexa", "nexa@example.com", "StrongPass123");
        when(userDao.existsByEmail("nexa@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
    }

    @Test
    void registerShouldFailWhenDatabaseUniqueConstraintIsHit() {
        RegisterRequestDto request = new RegisterRequestDto("Nexa", "nexa@example.com", "StrongPass123");
        AppUser mapped = user(1L, "Nexa", "nexa@example.com", "hash", UserRole.USER);

        when(userDao.existsByEmail("nexa@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass123")).thenReturn("hash");
        when(authDtoMapper.toNewUser(request, "nexa@example.com", "hash")).thenReturn(mapped);
        when(userDao.save(mapped)).thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequestDto request = new LoginRequestDto("nexa@example.com", "StrongPass123");
        AppUser user = user(1L, "Nexa", "nexa@example.com", "hash", UserRole.USER);

        when(userDao.findByEmail("nexa@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponseDto response = authService.login(request);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("nexa@example.com", "StrongPass123"));
        assertEquals("jwt-token", response.token());
        assertEquals(3600L, response.expiresInSeconds());
    }

    @Test
    void loginShouldFailWhenCredentialsAreInvalid() {
        LoginRequestDto request = new LoginRequestDto("nexa@example.com", "wrong");
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
