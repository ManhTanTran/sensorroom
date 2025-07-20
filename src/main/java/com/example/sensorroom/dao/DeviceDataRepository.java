// DeviceDataRepository.java
package com.example.sensorroom.dao;

import com.example.sensorroom.entity.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceDataRepository extends JpaRepository<DeviceData, Long> {
    List<DeviceData> findByDeviceId(Long deviceId);
}
