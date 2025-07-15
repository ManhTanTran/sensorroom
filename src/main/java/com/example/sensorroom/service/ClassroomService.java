package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.request.ClassroomRequest;

public interface ClassroomService {
    
    List<Classroom> getAllClassroom();

    Classroom getClassroom(Long id);

    List<Classroom> getClassroomsByUser(Long userId);

    Classroom createClassroom(Long userId, String name);

    Classroom updateClassroom(Long id, ClassroomRequest classroomRequest);

    void deleteClassroom(Long id);
}
