package com.yoga.firesafety.backend.web.controller;

import com.yoga.firesafety.backend.domain.entity.Role;
import com.yoga.firesafety.backend.domain.entity.User;
import com.yoga.firesafety.backend.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService service;

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PatchMapping("/{id}/role")
    public User updateUserRole(@PathVariable UUID id, @RequestParam Role role) {
        return service.updateUserRole(id, role);
    }
}
