package com.example.sensorroom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequest {
    @NotBlank
    private String type;

    @NotBlank
    @Size(min = 10, max = 255)
    private String message;

    @NotBlank
    @Size(max = 45)
    private String alert_type;

    private Boolean is_resolved;
    
}
