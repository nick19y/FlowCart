package com.flowcart.userservice.controller;

import com.flowcart.userservice.dto.UserRequest;
import com.flowcart.userservice.dto.UserResponse;
import com.flowcart.userservice.entity.User;
import com.flowcart.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserResponse create(@RequestBody UserRequest request) {
        User user = service.createUser(request);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return service.getAllUsers().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        User user = service.getUserById(id);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UserRequest request) {
        User user = service.updateUser(id, request);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteUser(id);
    }
}