package com.flowcart.userservice.service;

import com.flowcart.userservice.dto.UserRequest;
import com.flowcart.userservice.dto.UserResponse;
import com.flowcart.userservice.entity.User;
import com.flowcart.userservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public User createUser(UserRequest request) {
        User user = new User(
                null,
                request.getName(),
                request.getEmail(),
                encoder.encode(request.getPassword())
        );
        return repository.save(user);
    }

    public List<UserResponse> getAllUsers() {
    return repository.findAll().stream()
            .map(user -> new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            ))
            .toList();
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        return repository.save(user);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}