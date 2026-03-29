package com.nexa.cda.authapp.user.controller;

import com.nexa.cda.authapp.common.api.ApiResponse;
import com.nexa.cda.authapp.user.dto.MeResponseDto;
import com.nexa.cda.authapp.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponseDto>> me(Authentication authentication) {
        MeResponseDto response = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Current user loaded", response));
    }
}
