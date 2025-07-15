package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.Classroom;

public interface ClassroomService {
    
    List<Classroom> getAllClassroom();

    Classroom getClasroom(Long id);

    Classroom createClassroom(Classroom classroom);

    void deleteClassroom(Long id);
}
