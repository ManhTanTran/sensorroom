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
                .co2(request.getCo2())
                .createdAt(LocalDateTime.now())
                .device(device)
                .classroom(classroom)
                .build();
    }

    public static DeviceDataResponse toResponse(DeviceData data) {
        Device device = data.getDevice();
        String deviceType = device.getType().name();

        return DeviceDataResponse.builder()
                .id(data.getId())
                .temperature("TEMPERATURE".equals(deviceType) ? data.getTemperature() : null)
                .humidity("HUMIDITY".equals(deviceType) ? data.getHumidity() : null)
                .light("LIGHT".equals(deviceType) ? data.getLight() : null)
                .co2("CO2".equals(deviceType) ? data.getCo2() : null)
                .createdAt(data.getCreatedAt())
                .deviceCode(device.getDeviceCode())
                .deviceName(device.getName())
                .classroomId(data.getClassroom().getId())
                .classroomName(data.getClassroom().getName())
                .build();
    }

}
