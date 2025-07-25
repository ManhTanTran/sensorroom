package com.example.sensorroom.dto.alert;

import java.time.LocalDateTime;

import com.example.sensorroom.entity.Alert.Status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertResponse {
    private Long id;
    private String alertType;
    private String message;
    private Status isResolved;
    private LocalDateTime createdAt;
    private Long classroomId;
    private String classroomName; 
    private Long deviceId;
    private String deviceName;  
}
