package com.example.smartroom.service;

import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;

import java.util.Arrays;
import java.util.List;

public class AuthenticationService {
    private final List<User> users = Arrays.asList(
            new User("admin", "admin", "Admin", Role.ADMIN, List.of("All")),
            new User("ktv", "ktv", "Nguyen Van A", Role.KTV, List.of("A302", "A303", "B101"))
    );

    public User login(String username, String password) {
        return users.stream()
                .filter(user -> user.username().equals(username) && user.password().equals(password))
                .findFirst()
                .orElse(null);
    }
}