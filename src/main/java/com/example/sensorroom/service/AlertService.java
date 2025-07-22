package com.example.sensorroom.service;

import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.DeviceData;

public interface AlertService {
    
    void createAlert(Device device, DeviceData deviceData, String message);
}
