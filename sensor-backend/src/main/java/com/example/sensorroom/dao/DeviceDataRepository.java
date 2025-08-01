// DeviceDataRepository.java
package com.example.sensorroom.dao;

import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.entity.DeviceType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceDataRepository extends JpaRepository<DeviceData, Long> {
    List<DeviceData> findByDeviceDeviceCode(String deviceCode);

    List<Device> findByDeviceType(DeviceType deviceType);
}
