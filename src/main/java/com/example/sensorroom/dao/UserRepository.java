package com.example.sensorroom.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sensorroom.entity.User;

public interface UserRepository extends JpaRepository <User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByClassroomId(Long classroomId);
}