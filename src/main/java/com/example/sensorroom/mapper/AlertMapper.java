package com.example.sensorroom.mapper;

import com.example.sensorroom.dto.alert.AlertRequest;
import com.example.sensorroom.dto.alert.AlertResponse;
import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.Device;

import java.time.LocalDateTime;

public class AlertMapper {
    public static Alert toEntity(AlertRequest request, Classroom classroom, Device device) {
        return Alert.builder()
                .alertType(request.getAlertType())
                .message(request.getMessage())
                .isResolved(request.getIsResolved())
                .createdAt(LocalDateTime.now())
                .classroom(classroom)
                .device(device)
                .build();
    }

    public static AlertResponse toResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .alertType(alert.getAlertType())
                .message(alert.getMessage())
                .isResolved(alert.getIsResolved())
                .createdAt(alert.getCreatedAt())
                .classroomId(alert.getClassroom().getId())
                .deviceId(alert.getDevice().getId())
                .build();
    }
}
