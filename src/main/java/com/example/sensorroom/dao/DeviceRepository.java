// DeviceRepository.java
package com.example.sensorroom.dao;

import com.example.sensorroom.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByClassroomId(Long classroomId);

    Optional<Device> findByDeviceId(String deviceId);
}
