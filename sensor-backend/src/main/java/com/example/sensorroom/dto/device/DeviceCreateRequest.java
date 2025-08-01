package com.example.sensorroom.dto.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCreateRequest {

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("classroomId")
    private Long classroomId;
}
