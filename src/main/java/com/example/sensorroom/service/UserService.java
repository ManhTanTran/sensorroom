package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.User;

public interface UserService {
    
    User getUser(Long id);

    List<User> getAllUsers();

    List<User> getUsersByClassroom(Long classroomId);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
