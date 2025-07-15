package com.example.sensorroom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.SensorType;
import com.example.sensorroom.request.DeviceRequest;
import com.example.sensorroom.service.DeviceService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    
    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices(){
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping ("/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable Long id){
        return ResponseEntity.ok(deviceService.getDevice(id));
    }

    @GetMapping ("/classroom/{classroomId}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id,
                                               @Valid @RequestBody DeviceRequest request) {
        Device device = new Device();
        device.setName(request.getName());
        device.setType(SensorType.valueOf(request.getType()));
        device.setStatus(request.getStatus());
        return ResponseEntity.ok(deviceService.updateDevice(id, device));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
