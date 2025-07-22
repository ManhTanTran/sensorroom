package com.example.sensorroom.service;

import java.util.List;


import com.example.sensorroom.dto.device.DeviceResponse;


public interface DeviceService {

    DeviceResponse getById(Long id);
    List<DeviceResponse> getAll();

    
}

