package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.request.ClassroomRequest;

public interface ClassroomService {
    
    List<Classroom> getAllClassroom();

    Classroom getClasroom(Long id);

    Classroom createClassroom(Classroom classroom);

    Classroom updateClassroom(Long id, ClassroomRequest classroomRequest);

    void deleteClassroom(Long id);
}
