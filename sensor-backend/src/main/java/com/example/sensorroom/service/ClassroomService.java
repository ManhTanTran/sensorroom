package com.example.sensorroom.service;

import com.example.sensorroom.dto.classroom.*;
import com.example.sensorroom.entity.Classroom;

import java.util.List;

public interface ClassroomService {

    List<ClassroomResponse> getAll();

    ClassroomResponse getById(Long id);

    Classroom createClassroom(Classroom classroom);

    ClassroomResponse updateByClassroomId(Long id, ClassroomUpdateRequest request);

    void delete(Long id);
}