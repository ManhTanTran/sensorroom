package com.example.sensorroom.dto.classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import com.example.sensorroom.entity.Classroom;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String building;

    @NotBlank
    private String floor;

    @NotNull
    private Classroom.Status status;

    private String note;


}

