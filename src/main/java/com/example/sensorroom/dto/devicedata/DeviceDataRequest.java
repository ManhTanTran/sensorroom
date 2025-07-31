package com.example.sensorroom.dto.devicedata;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private String deviceId;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @JsonProperty("timestamp")
    private LocalDateTime createdAt;


    
}
