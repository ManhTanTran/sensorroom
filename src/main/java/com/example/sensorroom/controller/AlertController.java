package com.example.sensorroom.controller;

import com.example.sensorroom.dto.alert.AlertRequest;
import com.example.sensorroom.dto.alert.AlertResponse;
import com.example.sensorroom.entity.Alert.Status;
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

    // 1. Danh sách tất cả cảnh báo (ADMIN toàn quyền, USER chỉ xem phòng học mình phụ trách)
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAll());
    }

    // 2. Xem chi tiết 1 cảnh báo
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getById(id));
    }

    // 3. Tạo cảnh báo mới
    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.create(request));
    }

    // 4. Đánh dấu cảnh báo đã được xử lý
    @PutMapping("/resolve/{id}")
    public ResponseEntity<Void> resolveAlert(@PathVariable Long id) {
        alertService.resolveAlert(id);
        return ResponseEntity.noContent().build();
    }

    // 5. Xóa cảnh báo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Lọc theo phòng học (classroomId) – ADMIN và USER chỉ thấy dữ liệu của mình
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<AlertResponse>> getByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(alertService.getByClassroomId(classroomId));
    }

    // 7. Lọc theo trạng thái cảnh báo (YES: đã xử lý, NO: chưa xử lý)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AlertResponse>> getByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(alertService.getByStatus(status));
    }

    // 8. Xem danh sách lịch sử cảnh báo (status = YES)
    @GetMapping("/history")
    public ResponseEntity<List<AlertResponse>> getResolvedAlerts() {
        return ResponseEntity.ok(alertService.getByStatus(Status.YES));
    }
}
