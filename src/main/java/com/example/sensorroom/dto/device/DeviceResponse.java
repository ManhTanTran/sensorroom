package com.example.sensorroom.dto.device;

import java.time.LocalDateTime;

import com.example.sensorroom.entity.DeviceStatus;
import com.example.sensorroom.entity.DeviceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceResponse {
    private Long id;
    private String classroomName;
    private String createdByName;
    private String name;
    private String imei;
    private DeviceType type;
    private DeviceStatus status;
    private Integer dataCycle;
    private String notes;
    private Long classroomId;
    private Long createdById;
    private LocalDateTime createdAt;
}
