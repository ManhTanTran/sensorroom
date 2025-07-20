package com.example.sensorroom.dto.classroom;

import lombok.*;

import com.example.sensorroom.entity.Classroom;

@Data
public class ClassroomUpdateRequest {

    private String name;

    private String building;

    private String floor;

    private Classroom.Status status;

    private String note;


}
