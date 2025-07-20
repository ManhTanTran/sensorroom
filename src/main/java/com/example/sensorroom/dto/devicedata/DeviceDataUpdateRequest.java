package com.example.sensorroom.dto.devicedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDataUpdateRequest {
    private Double temperature;
    private Double humidity;
    private Double light;
    private Long deviceId;
    private Long classroomId;
}

