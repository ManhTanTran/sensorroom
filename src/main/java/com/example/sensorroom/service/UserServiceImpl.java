package com.example.sensorroom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.UserRepository;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.RoleType;
import com.example.sensorroom.entity.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        if (user.getClassroom() != null) {
            Classroom classroom = classroomRepository.findById(user.getClassroom().getId())
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
            user.setClassroom(classroom);
        }
        user.setRole(RoleType.valueOf(user.getRole().name()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User user = getUser(id);
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());

        if (updatedUser.getClassroom() != null) {
            Classroom classroom = classroomRepository.findById(updatedUser.getClassroom().getId())
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
            user.setClassroom(classroom);
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
