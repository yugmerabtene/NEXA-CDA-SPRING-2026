package com.nexa.cda.authapp.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nexa.cda.authapp.common.exception.UserNotFoundException;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.model.UserRole;
import com.nexa.cda.authapp.user.repository.UserRepository;
import com.nexa.cda.authapp.user.view.MeResponse;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnCurrentUserView() {
        AppUser user = new AppUser();
        user.setUsername("nexa-user");
        user.setEmail("nexa.user@example.com");
        user.setPassword("hash");
        user.setRole(UserRole.USER);
        user.setCreatedAt(Instant.parse("2026-03-29T10:00:00Z"));

        try {
            var field = AppUser.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, 10L);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

        when(userRepository.findByEmail("nexa.user@example.com")).thenReturn(Optional.of(user));

        MeResponse response = userService.getCurrentUser("nexa.user@example.com");

        assertEquals(10L, response.id());
        assertEquals("nexa.user@example.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser("missing@example.com"));
    }
}
