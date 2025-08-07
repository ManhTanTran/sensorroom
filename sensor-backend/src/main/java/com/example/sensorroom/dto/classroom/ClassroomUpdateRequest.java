package com.example.sensorroom.dto.classroom;

import lombok.*;

import com.example.sensorroom.entity.constant.Roomtype;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomUpdateRequest {

    private String name;

    private String building;

    private String floor;

    private Roomtype roomtype;


}

