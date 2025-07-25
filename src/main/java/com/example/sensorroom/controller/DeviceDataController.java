package com.example.sensorroom.controller;

import com.example.sensorroom.dto.devicedata.DeviceDataRequest;
import com.example.sensorroom.dto.devicedata.DeviceDataResponse;
import com.example.sensorroom.service.DeviceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/device-data")
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceDataService deviceDataService;

    @GetMapping
    public ResponseEntity<List<DeviceDataResponse>> getAll() {
        return ResponseEntity.ok(deviceDataService.getAll());
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceDataResponse>> getByDeviceId(@PathVariable Long deviceId) {
        return ResponseEntity.ok(deviceDataService.getByDeviceId(deviceId));
    }

    @PostMapping
    public ResponseEntity<DeviceDataResponse> create(@Valid @RequestBody DeviceDataRequest request) {
        return ResponseEntity.ok(deviceDataService.create(request));
    }
}
