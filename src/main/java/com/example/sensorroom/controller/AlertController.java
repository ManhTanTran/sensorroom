package com.example.sensorroom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.request.AlertRequest;
import com.example.sensorroom.service.AlertService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlert(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlert(id));
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<Alert>> getAlertsByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(alertService.getAlertsByDevice(deviceId));
    }

    @GetMapping("/status/{resolved}")
    public ResponseEntity<List<Alert>> getAlertsByStatus(@PathVariable Boolean resolved) {
        return ResponseEntity.ok(alertService.getAlertsByResolvedStatus(resolved));
    }

    @PostMapping("/device/{deviceId}")
    public ResponseEntity<Alert> createAlert(@PathVariable Long deviceId,
                                             @Valid @RequestBody AlertRequest request) {
        Alert alert = new Alert();
        alert.setAlertType(request.getAlertType());
        alert.setMessage(request.getMessage());
        alert.setIsResolved(request.getIsResolved());
        return ResponseEntity.ok(alertService.createAlert(deviceId, alert));
    }

    @PutMapping("/resolve/{id}")
    public ResponseEntity<Alert> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.resolveAlert(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}