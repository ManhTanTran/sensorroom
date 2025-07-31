package com.example.sensorroom.mapper;

import com.example.sensorroom.dto.classroom.ClassroomRequest;
import com.example.sensorroom.dto.classroom.ClassroomResponse;
import com.example.sensorroom.entity.Classroom;

public class ClassroomMapper {

    public static Classroom toEntity(ClassroomRequest request) {
        return Classroom.builder()
                .code(request.getCode())
                .name(request.getName())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .status(request.getStatus())
                .note(request.getNote())
                .build(); // createdAt và active sẽ được set ở entity (@PrePersist hoặc default)
    }


    public static ClassroomResponse toResponse(Classroom classroom) {
        return ClassroomResponse.builder()
                .id(classroom.getId())
                .code(classroom.getCode())
                .name(classroom.getName())
                .building(classroom.getBuilding())
                .floor(classroom.getFloor())
                .status(classroom.getStatus())
                .note(classroom.getNote())
                .active(classroom.getActive())
                .createdAt(classroom.getCreatedAt())
                .deviceCount(
                        classroom.getDevices() != null ? classroom.getDevices().size() : 0
                )
                .build();
    }
}
