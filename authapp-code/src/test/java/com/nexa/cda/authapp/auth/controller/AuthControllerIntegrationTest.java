package com.nexa.cda.authapp.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanData() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        String body = """
                {
                  "username": "nexa-user",
                  "email": "nexa.user@example.com",
                  "password": "StrongPass123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("nexa.user@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        String body = """
                {
                  "username": "nexa-user",
                  "email": "duplicate@example.com",
                  "password": "StrongPass123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExistsIgnoringCase() throws Exception {
        String firstBody = """
                {
                  "username": "nexa-user",
                  "email": "duplicate@example.com",
                  "password": "StrongPass123"
                }
                """;

        String secondBody = """
                {
                  "username": "nexa-user-2",
                  "email": "DUPLICATE@EXAMPLE.COM",
                  "password": "StrongPass123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void shouldReturnValidationErrorForInvalidPayload() throws Exception {
        String body = """
                {
                  "username": "ab",
                  "email": "invalid",
                  "password": "123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void shouldReturnValidationErrorForInvalidLoginPayload() throws Exception {
        String body = """
                {
                  "email": "invalid",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldNormalizeEmailAndUsernameOnRegister() throws Exception {
        String body = """
                {
                  "username": "  nexa-user  ",
                  "email": "NEXA.USER@EXAMPLE.COM",
                  "password": "StrongPass123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("nexa-user"))
                .andExpect(jsonPath("$.data.email").value("nexa.user@example.com"));
    }

    @Test
    void shouldStoreHashedPasswordInDatabase() throws Exception {
        String rawPassword = "StrongPass123";
        String body = """
                {
                  "username": "hash-check",
                  "email": "hash.check@example.com",
                  "password": "StrongPass123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        AppUser user = userRepository.findByEmail("hash.check@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotEquals(rawPassword, user.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(user.getPassword().startsWith("$2"));
    }
}
