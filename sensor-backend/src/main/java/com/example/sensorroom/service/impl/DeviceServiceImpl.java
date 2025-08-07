package com.example.sensorroom.service.impl;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.dto.device.DeviceUpdateRequest;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.constant.DeviceStatus;
import com.example.sensorroom.exception.ResourceNotFoundException;
import com.example.sensorroom.exception.BadRequestException;
import com.example.sensorroom.mapper.DeviceMapper;
import com.example.sensorroom.service.DeviceService;
import com.example.sensorroom.service.MqttPublisherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final MqttPublisherService mqttPublisherService;
    private final ClassroomRepository classroomRepository;


    @Override
    public DeviceResponse getById(Long id) {
        return DeviceMapper.toResponse(deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found")));
    }

    @Override
    public List<DeviceResponse> getAll() {
        return deviceRepository.findAll()
                .stream()
                .map(DeviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public DeviceResponse updateByDeviceCode(String deviceCode, DeviceUpdateRequest request) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        Classroom classroom = null;
        if (request.getClassroomId() != null) {
            classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));
        }

        DeviceMapper.updateDeviceFromRequest(device, request, classroom);
        deviceRepository.save(device);

        return DeviceMapper.toResponse(device);
    }

    @Override
    public void updateDeviceStatus(String deviceCode, DeviceStatus newStatus) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        DeviceStatus currentStatus = device.getStatus();

        if (currentStatus == newStatus) {
            throw new BadRequestException("Device is already " + newStatus.name());
        }

        // Cập nhật trạng thái mới
        device.setStatus(newStatus);
        deviceRepository.save(device);

        Long classroomId = device.getClassroom().getId();

        // Gửi lệnh MQTT tương ứng
        if (newStatus == DeviceStatus.ACTIVE) {
            mqttPublisherService.publishDeviceCreate(deviceCode, classroomId);
        } else if (newStatus == DeviceStatus.INACTIVE) {
            mqttPublisherService.publishDeviceDelete(deviceCode, classroomId); // dừng thiết bị
        }
    }

    
}

