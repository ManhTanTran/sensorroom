package com.example.sensorroom.dto.devicedata;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDataRequest {
    
    private Double temperature;

    private Double humidity;

    private Double light;

    private Double co2;

    @NotNull(message = "Device ID is required")
    private Long deviceId;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    
}
