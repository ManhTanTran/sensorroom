package com.example.sensorroom.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sensorroom.entity.DeviceData;

public interface DeviceDataRepository extends JpaRepository <DeviceData, Long>{
    
    List<DeviceData> findByDeviceId (Long deviceId);
} 
