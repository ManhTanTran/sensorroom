package com.example.sensorroom.service.impl;

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
import com.example.sensorroom.exception.ResourceNotFoundException;
import com.example.sensorroom.mapper.DeviceDataMapper;
import com.example.sensorroom.service.AlertService;
import com.example.sensorroom.service.DeviceDataService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceDataServiceImpl implements DeviceDataService {

    private final DeviceDataRepository deviceDataRepository;
    private final DeviceRepository deviceRepository;
    private final ClassroomRepository classroomRepository;
    private final AlertService alertService;

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
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));

        DeviceData deviceData = DeviceDataMapper.toEntity(request, device, classroom);
        deviceData.setCreatedAt(LocalDateTime.now());

        deviceDataRepository.save(deviceData);

        processAlert(device, deviceData, request);

        return DeviceDataMapper.toResponse(deviceData);
    }

    private void processAlert(Device device, DeviceData deviceData, DeviceDataRequest request) {
        switch (device.getType()) {
            case TEMPERATURE -> handleTemperatureAlert(device, deviceData, request.getTemperature());
            case HUMIDITY    -> handleHumidityAlert(device, deviceData, request.getHumidity());
            case LIGHT       -> handleLightAlert(device, deviceData, request.getLight());
            case CO2         -> handleCo2Alert(device, deviceData, request.getCo2());
        }
    }

    private void handleTemperatureAlert(Device device, DeviceData deviceData, Double temp) {
        if (temp == null) return;

        if (temp < 18 || temp > 30) {
            alertService.createAlert(device, deviceData, "Vượt ngưỡng");
        } else if ((temp >= 18 && temp <= 21.9) || (temp >= 27.1 && temp <= 30)) {
            alertService.createAlert(device, deviceData, "Trung bình");
        } else {
            alertService.createAlert(device, deviceData, "Tốt");
        }
    }

    private void handleHumidityAlert(Device device, DeviceData deviceData, Double humidity) {
        if (humidity == null) return;

        if (humidity < 35 || humidity > 70) {
            alertService.createAlert(device, deviceData, "Vượt ngưỡng");
        } else if ((humidity >= 35 && humidity <= 44) || (humidity > 60 && humidity <= 70)) {
            alertService.createAlert(device, deviceData, "Trung bình");
        } else {
            alertService.createAlert(device, deviceData, "Tốt");
        }
    }

    private void handleLightAlert(Device device, DeviceData deviceData, Double light) {
        if (light == null) return;

        if (light > 1500) {
            alertService.createAlert(device, deviceData, "Vượt ngưỡng");
        } else if ((light >= 200 && light <= 299) || (light >= 501 && light <= 600)) {
            alertService.createAlert(device, deviceData, "Trung bình");
        } else {
            alertService.createAlert(device, deviceData, "Tốt");
        }
    }

    private void handleCo2Alert(Device device, DeviceData deviceData, Double co2) {
        if (co2 == null) return;

        if (co2 > 1500) {
            alertService.createAlert(device, deviceData, "Vượt ngưỡng");
        } else if (co2 >= 1001 && co2 <= 1500) {
            alertService.createAlert(device, deviceData, "Trung bình");
        } else {
            alertService.createAlert(device, deviceData, "Tốt");
        }
    }
}
