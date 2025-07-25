package com.example.sensorroom.dto.alert;

import com.example.sensorroom.entity.Alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertUpdateRequest {
    private String message;
    private Alert.Status isResolved;  // Enum YES/NO
}

