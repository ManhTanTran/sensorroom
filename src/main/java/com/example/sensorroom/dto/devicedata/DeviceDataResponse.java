package com.example.sensorroom.dto.devicedata;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceDataResponse {
    private Long id;
    private Double temperature;
    private Double humidity;
    private Double light;
    private LocalDateTime createdAt;
    private Long deviceId;
    private String deviceName;
    private Long classroomId;
    private String classroomName;
}