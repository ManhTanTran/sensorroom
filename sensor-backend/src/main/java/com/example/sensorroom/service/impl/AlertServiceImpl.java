package com.example.sensorroom.service.impl;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.AlertRepository;
import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.service.AlertService;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository){
        this.alertRepository = alertRepository;
    }

     @Override
    public void createAlert(Device device, DeviceData devicedata, String message) {
        Alert alert = Alert.builder()
                .device(device)
                .deviceData(devicedata)
                .message(message)
                .build();
        alertRepository.save(alert);
    }
    
}
