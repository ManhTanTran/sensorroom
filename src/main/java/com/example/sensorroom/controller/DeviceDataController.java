package com.example.sensorroom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.request.DeviceDataRequest;
import com.example.sensorroom.service.DeviceDataService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DeviceDataController {
    
    private final DeviceDataService deviceDataService;

    @GetMapping("/id")
    public ResponseEntity<DeviceData> getData(@PathVariable Long id){
        return ResponseEntity.ok(deviceDataService.getData(id));
    }

    @GetMapping
    public ResponseEntity<List<DeviceData>> getAllData() {
        return ResponseEntity.ok(deviceDataService.getAllData());
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceData>> getDataByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(deviceDataService.getDataByDevice(deviceId));
    }

    @PostMapping("/device/{deviceId}")
    public ResponseEntity<DeviceData> createData(@PathVariable Long deviceId,
                                                 @Valid @RequestBody DeviceDataRequest request) {
        DeviceData data = new DeviceData();
        data.setDataType(request.getDataType());
        data.setValue(request.getValue());
        return ResponseEntity.ok(deviceDataService.createData(deviceId, data));
    }
}
