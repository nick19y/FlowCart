package com.flowcart.userservice.controller;

import com.flowcart.userservice.dto.AuthRequest;
import com.flowcart.userservice.dto.AuthResponse;
import com.flowcart.userservice.dto.UserRequest;
import com.flowcart.userservice.dto.UserResponse;
import com.flowcart.userservice.entity.User;
import com.flowcart.userservice.service.AuthService;
import com.flowcart.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}