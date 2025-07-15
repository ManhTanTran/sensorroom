package com.example.sensorroom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceRequest {
    @NotBlank
    @Size(max = 45)
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    @Size(max = 20)
    private String status;
}
