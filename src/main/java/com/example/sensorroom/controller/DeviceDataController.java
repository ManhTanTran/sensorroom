package com.example.sensorroom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.request.DeviceDataRequest;
import com.example.sensorroom.service.DeviceDataService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DeviceDataController {
    
    private final DeviceDataService deviceDataService;

    @GetMapping("/{id}")
    public ResponseEntity<DeviceData> getData(@PathVariable Long id) {
        return ResponseEntity.ok(deviceDataService.getData(id));
    }

    @GetMapping
    public ResponseEntity<List<DeviceData>> getAllData() {
        return ResponseEntity.ok(deviceDataService.getAllData());
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<DeviceData>> getDataByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(deviceDataService.getDataByClassroom(classroomId));
    }

    @PostMapping("/classroom/{classroomId}")
    public ResponseEntity<DeviceData> createData(@PathVariable Long classroomId,
                                                 @Valid @RequestBody DeviceDataRequest request) {
        return ResponseEntity.ok(deviceDataService.createData(classroomId, request));
    }
}
