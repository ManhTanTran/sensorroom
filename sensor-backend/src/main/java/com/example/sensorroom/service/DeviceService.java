package com.example.sensorroom.service;

import java.util.List;


import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.dto.device.DeviceUpdateRequest;


public interface DeviceService {

    DeviceResponse getById(Long id);

    List<DeviceResponse> getAll();

    void delete(Long id);

    void activeDevice(String deviceCode);

    DeviceResponse updateByDeviceCode(String deviceCode, DeviceUpdateRequest request);


    
}

