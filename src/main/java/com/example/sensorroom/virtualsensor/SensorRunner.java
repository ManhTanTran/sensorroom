package com.example.sensorroom.virtualsensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SensorRunner {

    @Autowired
    private SensorService sensorService;

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void run() {
        sensorService.sendSensorData();
    }
}

