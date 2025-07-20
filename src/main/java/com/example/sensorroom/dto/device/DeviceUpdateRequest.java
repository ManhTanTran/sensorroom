package com.example.sensorroom.dto.device;

import com.example.sensorroom.entity.DeviceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceUpdateRequest {
    private String name;
    private DeviceStatus status;
    private Integer dataCycle;
    private String notes;
    private Long classroomId;
}
