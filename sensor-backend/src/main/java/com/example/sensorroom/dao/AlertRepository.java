package com.example.sensorroom.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sensorroom.entity.Alert;


public interface AlertRepository extends JpaRepository<Alert, Long> {
    
} 