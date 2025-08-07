package com.example.smartroom.service;

import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;

import java.util.Arrays;
import java.util.List;

public class AuthenticationService {
    private final List<User> users = Arrays.asList(
            new User("admin", "admin", "Admin", Role.ADMIN, List.of("All")),
            // KTV quản lý nhiều phòng
            new User("ktv_multi", "ktv", "Nguyen Van A", Role.KTV, List.of("101 A", "201 A", "103 B")),
            // KTV chỉ quản lý 1 phòng
            new User("ktv_single", "ktv", "Tran Thi B", Role.KTV, List.of("104 B"))
    );

    public User login(String username, String password) {
        return users.stream()
                .filter(user -> user.username().equals(username) && user.password().equals(password))
                .findFirst()
                .orElse(null);
    }
}