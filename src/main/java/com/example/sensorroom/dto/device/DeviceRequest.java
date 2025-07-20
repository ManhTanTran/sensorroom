package com.example.sensorroom.dto.device;

import com.example.sensorroom.entity.DeviceStatus;
import com.example.sensorroom.entity.DeviceType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceRequest {
    
    @NotBlank(message = "Device name is required")
    private String name;

    @NotBlank(message = "IMEI is required")
    private String imei;

    @NotNull(message = "Device type is required")
    private DeviceType type;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @NotNull(message = "User ID (createdBy) is required")
    private Long createdBy;

    @NotBlank(message = "Status is required") 
    private DeviceStatus status;

    @NotNull(message = "Data cycle must be provided")
    @Min(value = 1, message = "Data cycle must be at least 1")
    private Integer dataCycle; 

    private String notes; 
}
