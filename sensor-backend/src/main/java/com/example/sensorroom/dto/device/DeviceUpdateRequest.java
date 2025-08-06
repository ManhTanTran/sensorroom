package com.example.sensorroom.dto.device;

import com.example.sensorroom.entity.DeviceStatus;
import com.example.sensorroom.entity.DeviceType;
import lombok.Data;

@Data
public class DeviceUpdateRequest {
    private String name;
    private DeviceType type;
    private DeviceStatus status;
    private Integer dataCycle;
    private String notes;
    private String classroomName;
    private Long classroomId;
}
