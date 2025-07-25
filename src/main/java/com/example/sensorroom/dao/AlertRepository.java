// AlertRepository.java
package com.example.sensorroom.dao;

import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Alert.Status;

import com.example.sensorroom.entity.Classroom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByDevice_Classroom(Classroom classroom);

    List<Alert> findByDevice_Classroom_Id(Long classroomId);

    List<Alert> findByIsResolved(Status status);

    List<Alert> findByIsResolvedAndDevice_Classroom(Status status, Classroom classroom);

    List<Alert> findByIsResolvedAndDevice_Classroom_Id(Status status, Long classroomId);
}
