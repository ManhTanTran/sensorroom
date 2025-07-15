package com.example.sensorroom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.Device;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService {
    
     private final DeviceRepository deviceRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public Device getDevice(Long id) {
        return deviceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Device not found"));
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public List<Device> getDevicesByClassroom(Long classroomId) {
        return deviceRepository.findByClassroomId(classroomId);
    }

    @Override
    public Device createDevice(Long classroomId, Device device) {
        Classroom classroom = classroomRepository.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
        device.setClassroom(classroom);
        return deviceRepository.save(device);
    }

    @Override
    public Device updateDevice(Long id, Device updatedDevice) {
        Device device = getDevice(id);
        device.setName(updatedDevice.getName());
        device.setType(updatedDevice.getType());
        device.setStatus(updatedDevice.getStatus());
        return deviceRepository.save(device);
    }

    @Override
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }
}
