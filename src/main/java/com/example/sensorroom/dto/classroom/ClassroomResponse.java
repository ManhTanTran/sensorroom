package com.example.sensorroom.dto.classroom;

import java.time.LocalDateTime;

import com.example.sensorroom.entity.Classroom;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassroomResponse {

    private Long id;

    private String code;

    private String name;

    private String building;

    private String floor;

    private Classroom.Status status;

    private String note;

    private Boolean active;

    private LocalDateTime createdAt;

    private int deviceCount;
}

