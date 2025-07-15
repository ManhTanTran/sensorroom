package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.Device;

public interface DeviceService {
    
    Device getDevice(Long id);

    List<Device> getAllDevices();

    List<Device> getDevicesByClassroom(Long classroomId);

    Device createDevice(Long classroomId, Device device);

    Device updateDevice(Long id, Device device);

    void deleteDevice(Long id);
}
