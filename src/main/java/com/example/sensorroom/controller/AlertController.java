package com.example.sensorroom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Alert.Status;
import com.example.sensorroom.request.AlertRequest;
import com.example.sensorroom.service.AlertService;

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

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<Alert>> getAlertsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(alertService.getAlertsByClassroom(classroomId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Alert>> getAlertsByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(alertService.getAlertsByResolvedStatus(status));
    }

    @PostMapping("/classroom/{classroomId}")
    public ResponseEntity<Alert> createAlert(@PathVariable Long classroomId,
                                             @Valid @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.createAlert(classroomId, request));
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
