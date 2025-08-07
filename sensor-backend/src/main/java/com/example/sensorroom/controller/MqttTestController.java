package com.example.sensorroom.controller;

import com.example.sensorroom.dto.device.DeviceCreateRequest;
import com.example.sensorroom.entity.constant.DeviceStatus;
import com.example.sensorroom.service.DeviceService;
import com.example.sensorroom.service.MqttPublisherService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mqtt")
@RequiredArgsConstructor
public class MqttTestController {

    private final MqttPublisherService mqttPublisherService;
    private final DeviceService deviceService;

    @PostMapping("/test-publish-create")
    public ResponseEntity<String> testPublishCreate(@RequestBody DeviceCreateRequest request) {
        mqttPublisherService.publishDeviceCreate(request);
        return ResponseEntity.ok("✅ Đã gửi DEVICE_CREATE lên MQTT (test)");
    }


    @PutMapping("/status")
    public ResponseEntity<?> updateDeviceStatus(
        @RequestParam("deviceCode") String deviceCode,
        @RequestParam("deviceStatus") DeviceStatus status
    ) {
        deviceService.updateDeviceStatus(deviceCode, status);
        return ResponseEntity.ok().build();
    }

}
