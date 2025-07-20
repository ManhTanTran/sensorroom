package com.example.sensorroom.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.dao.UserRepository;
import com.example.sensorroom.dto.device.DeviceRequest;
import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.dto.device.DeviceUpdateRequest;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.User;
import com.example.sensorroom.mapper.DeviceMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    @Override
    public DeviceResponse getById(Long id) {
        return DeviceMapper.toResponse(deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found")));
    }

    @Override
    public List<DeviceResponse> getAll() {
        return deviceRepository.findAll()
                .stream()
                .map(DeviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
public DeviceResponse create(DeviceRequest request) {
    Classroom classroom = classroomRepository.findById(request.getClassroomId())
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

    User creator = userRepository.findById(request.getCreatedBy())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Device device = DeviceMapper.toEntity(request, creator, classroom);
    return DeviceMapper.toResponse(deviceRepository.save(device));
}

    @Override
    public DeviceResponse update(Long id, DeviceUpdateRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        DeviceMapper.updateEntity(device, request);
        return DeviceMapper.toResponse(deviceRepository.save(device));
    }

    @Override
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }
}

