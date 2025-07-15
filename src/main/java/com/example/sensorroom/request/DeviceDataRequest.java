package com.example.sensorroom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDataRequest {
    @NotBlank
    @Size(max = 50)
    private String dataType;

    @NotBlank
    private double value;

    
}
