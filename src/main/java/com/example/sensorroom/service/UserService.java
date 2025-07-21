package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.dto.user.UserRequest;
import com.example.sensorroom.dto.user.UserResponse;
import com.example.sensorroom.dto.user.UserUpdateRequest;

public interface UserService {
    
    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);
    
    void deleteUser(Long id);
}
