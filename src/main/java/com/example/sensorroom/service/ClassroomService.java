package com.example.sensorroom.service;

import com.example.sensorroom.dto.classroom.*;

import java.util.List;

public interface ClassroomService {

    ClassroomResponse create(ClassroomRequest request);

    ClassroomResponse update(Long id, ClassroomUpdateRequest request);

    void delete(Long id);

    List<ClassroomResponse> getAll();

    ClassroomResponse getById(Long id);
}