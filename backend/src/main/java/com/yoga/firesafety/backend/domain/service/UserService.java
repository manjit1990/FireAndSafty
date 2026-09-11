package com.yoga.firesafety.backend.domain.service;

import com.yoga.firesafety.backend.domain.entity.Role;
import com.yoga.firesafety.backend.domain.entity.User;
import com.yoga.firesafety.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Transactional
    public User updateUserRole(UUID userId, Role newRole) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return repository.save(user);
    }
}
