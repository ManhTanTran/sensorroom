package com.example.sensorroom.dto.devicedata;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDataRequest {
    
    @NotNull(message = "Temperature is required")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    private Double humidity;

    @NotNull(message = "Light level is required")
    private Double light;

    @NotNull(message = "Device ID is required")
    private Long deviceId;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    
}
