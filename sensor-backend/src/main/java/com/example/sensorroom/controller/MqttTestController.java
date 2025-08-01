package com.example.sensorroom.controller;

import com.example.sensorroom.dto.device.DeviceCreateRequest;
import com.example.sensorroom.service.MqttPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mqtt")
@RequiredArgsConstructor
public class MqttTestController {

    private final MqttPublisherService mqttPublisherService;

    @PostMapping("/publish-device")
    public String publishDevice(@RequestBody DeviceCreateRequest request) {
        mqttPublisherService.publishDeviceCreate(request);
        return "Đã gửi lên MQTT.";
    }
}
