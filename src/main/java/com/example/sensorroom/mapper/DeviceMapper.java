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
                .imei(request.getImei())
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
            .imei(device.getImei())
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

    public static void updateEntity(Device device, DeviceUpdateRequest request) {
        if (request.getName() != null) device.setName(request.getName());
        if (request.getStatus() != null) device.setStatus(request.getStatus());
        if (request.getDataCycle() != null) device.setDataCycle(request.getDataCycle());
        if (request.getNotes() != null) device.setNotes(request.getNotes());
        if (request.getClassroomId() != null) {
            Classroom classroom = new Classroom();
            classroom.setId(request.getClassroomId());
            device.setClassroom(classroom);
        }
    }
}