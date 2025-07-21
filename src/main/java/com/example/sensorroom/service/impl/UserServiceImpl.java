package com.example.sensorroom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.UserRepository;
import com.example.sensorroom.dto.user.UserRequest;
import com.example.sensorroom.dto.user.UserResponse;
import com.example.sensorroom.dto.user.UserUpdateRequest;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.User;
import com.example.sensorroom.mapper.UserMapper;
import com.example.sensorroom.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public UserResponse getUserById(Long id) {
        return UserMapper.toResponse(userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = UserMapper.toEntity(request);
        if (request.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
            user.setClassroom(classroom);
        }
        user.setCreatedAt(new Date(System.currentTimeMillis()));
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
            user.setClassroom(classroom);
        }

        UserMapper.updateEntity(user, request);

        return UserMapper.toResponse(userRepository.save(user));
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    
}

