package com.example.sensorroom.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.DeviceDataRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.dto.devicedata.DeviceDataRequest;
import com.example.sensorroom.dto.devicedata.DeviceDataResponse;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.mapper.DeviceDataMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceDataServiceImpl implements DeviceDataService {
    private final DeviceDataRepository deviceDataRepository;
    private final DeviceRepository deviceRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public List<DeviceDataResponse> getAll() {
        return deviceDataRepository.findAll()
                .stream()
                .map(DeviceDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceDataResponse> getByDeviceId(Long deviceId) {
        return deviceDataRepository.findByDeviceId(deviceId)
                .stream()
                .map(DeviceDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceDataResponse create(DeviceDataRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        DeviceData deviceData = DeviceDataMapper.toEntity(request, device, classroom);
        deviceData.setDevice(device);
        deviceData.setClassroom(classroom);
        deviceData.setCreatedAt(LocalDateTime.now());

        return DeviceDataMapper.toResponse(deviceDataRepository.save(deviceData));
    }
}

