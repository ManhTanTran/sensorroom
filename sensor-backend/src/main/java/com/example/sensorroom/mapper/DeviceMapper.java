package com.example.sensorroom.mapper;

import com.example.sensorroom.entity.Device;
import com.example.sensorroom.dto.device.DeviceRequest;
import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.dto.device.DeviceUpdateRequest;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.User;


import java.time.LocalDateTime;

public class DeviceMapper {
    public static Device toEntity(DeviceRequest request, User createdBy, Classroom classroom) {
        return Device.builder()
                .name(request.getName())
                .deviceCode(request.getDeviceCode())
                .type(request.getType())
                .status(request.getStatus())
                .dataCycle(request.getDataCycle())
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .classroom(classroom)
                .build();
    }

    public static DeviceResponse toResponse(Device device) {
        return DeviceResponse.builder()
            .id(device.getId())
            .deviceCode(device.getDeviceCode())
            .name(device.getName())
            .type(device.getType())
            .status(device.getStatus())
            .dataCycle(device.getDataCycle())
            .notes(device.getNotes())
            .createdAt(device.getCreatedAt())
            .classroomId(device.getClassroom() != null ? device.getClassroom().getId() : null)
            .classroomName(device.getClassroom() != null ? device.getClassroom().getName() : null)
            .createdById(device.getCreatedBy() != null ? device.getCreatedBy().getId() : null)
            .createdByName(device.getCreatedBy() != null ? device.getCreatedBy().getFullName() : null)
            .build();
    }

    public static void updateDeviceFromRequest(Device device, DeviceUpdateRequest request, Classroom classroom) {
        device.setName(request.getName());
        device.setType(request.getType());
        device.setStatus(request.getStatus());
        device.setDataCycle(request.getDataCycle());
        device.setNotes(request.getNotes());

        if (classroom != null) {
            device.setClassroom(classroom);
        }
    }

}