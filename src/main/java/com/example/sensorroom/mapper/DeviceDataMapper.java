package com.example.sensorroom.mapper;

import com.example.sensorroom.entity.Device;
import com.example.sensorroom.dto.devicedata.DeviceDataRequest;
import com.example.sensorroom.dto.devicedata.DeviceDataResponse;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.DeviceData;


import java.time.LocalDateTime;

public class DeviceDataMapper {
    public static DeviceData toEntity(DeviceDataRequest request, Device device, Classroom classroom) {
        return DeviceData.builder()
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .light(request.getLight())
                .createdAt(LocalDateTime.now())
                .device(device)
                .classroom(classroom)
                .build();
    }

    public static DeviceDataResponse toResponse(DeviceData data) {
    return DeviceDataResponse.builder()
            .id(data.getId())
            .temperature(data.getTemperature())
            .humidity(data.getHumidity())
            .light(data.getLight())
            .createdAt(data.getCreatedAt())
            .deviceId(data.getDevice().getId())
            .deviceName(data.getDevice().getName())
            .classroomId(data.getClassroom().getId())
            .classroomName(data.getClassroom().getName())
            .build();
}
}
