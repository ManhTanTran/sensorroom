package com.example.sensorroom.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sensorroom.entity.Device;

public interface DeviceRepository extends JpaRepository <Device, Long> {

    List<Device> findByClassroomId(Long classroomId);
    
} 
