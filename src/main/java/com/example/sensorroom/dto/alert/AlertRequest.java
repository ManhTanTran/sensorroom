package com.example.sensorroom.dto.alert;

import com.example.sensorroom.entity.Alert.Status;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequest {
    @NotBlank
    @Size(max = 45)
    private String alertType;

    @NotBlank
    @Size(min = 10, max = 255)
    private String message;

    @NotNull
    private Long classroomId;

    @NotNull
    private Long deviceId;

    @Column(nullable = false)
    private Status isResolved;
}
