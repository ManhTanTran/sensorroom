package com.example.sensorroom.dao;

import com.example.sensorroom.entity.Alert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByDeviceId(Long deviceId);

    List<Alert> findByIsResolved(Boolean isResolved);
}
