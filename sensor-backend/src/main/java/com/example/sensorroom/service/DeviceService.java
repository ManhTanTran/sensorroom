package com.example.sensorroom.service;

import java.util.List;


import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.dto.device.DeviceUpdateRequest;
import com.example.sensorroom.entity.constant.DeviceStatus;


public interface DeviceService {

    DeviceResponse getById(Long id);

    List<DeviceResponse> getAll();

    void delete(Long id);

    void updateDeviceStatus(String deviceCode, DeviceStatus newStatus);

    DeviceResponse updateByDeviceCode(String deviceCode, DeviceUpdateRequest request);


    
}

