package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.dto.device.DeviceRequest;
import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.dto.device.DeviceUpdateRequest;

public interface DeviceService {

    DeviceResponse getById(Long id);
    List<DeviceResponse> getAll();

    DeviceResponse create(DeviceRequest request);
    
    DeviceResponse update(Long id, DeviceUpdateRequest request);
    void delete(Long id);
}

