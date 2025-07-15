package com.example.sensorroom.dao;

import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Alert.Status;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByClassroomId(Long classroomId);

    List<Alert> findByIsResolved(Status status);
}
