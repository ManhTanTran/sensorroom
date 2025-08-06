package com.example.sensorroom.controller;

import com.example.sensorroom.dto.device.DeviceCreateRequest;
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

    @PostMapping("/publish-device")
    public String publishDevice(@RequestBody DeviceCreateRequest request) {
        mqttPublisherService.publishDeviceCreate(request);
        return "Đã gửi lên MQTT.";
    }

    @PostMapping("/control-device")
    public ResponseEntity<String> controlDevice(
        @RequestParam(name = "deviceCode") String deviceCode,
        @RequestParam(name = "classroomId") Long classroomId) {
        deviceService.activeDevice(deviceCode); // đã kiểm tra status bên trong service
        return ResponseEntity.ok("✅ Thiết bị đã được kích hoạt và gửi lệnh MQTT.");
    }



}
