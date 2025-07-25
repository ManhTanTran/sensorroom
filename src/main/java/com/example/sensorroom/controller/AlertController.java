package com.example.sensorroom.controller;

import com.example.sensorroom.dto.alert.AlertRequest;
import com.example.sensorroom.dto.alert.AlertResponse;
import com.example.sensorroom.entity.Alert.Status;
import com.example.sensorroom.entity.User;
import com.example.sensorroom.service.AlertService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    private User getCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Demo User");
        user.setAccountType(com.example.sensorroom.entity.RoleType.ADMIN);  // hoặc USER tùy bạn
        return user;
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(alertService.getAll(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(alertService.getById(id, currentUser));
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(alertService.create(request, currentUser));
    }

    @PutMapping("/resolve/{id}")
    public ResponseEntity<Void> resolveAlert(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        alertService.resolveAlert(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        alertService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<AlertResponse>> getByClassroom(@PathVariable Long classroomId) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(alertService.getByClassroomId(classroomId, currentUser));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AlertResponse>> getByStatus(@PathVariable Status status) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(alertService.getByStatus(status, currentUser));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AlertResponse>> getResolvedAlerts() {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(alertService.getByStatus(Status.YES, currentUser));
    }
}
