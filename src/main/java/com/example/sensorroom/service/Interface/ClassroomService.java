package com.example.sensorroom.service.Interface;

import java.util.List;

import com.example.sensorroom.entity.Classroom;

public interface ClassroomService {
    
    List<Classroom> getAllClassroom();

    Classroom getClasroom(Long id);

    Classroom createClassroom(Classroom classroom);

    void deleteClassroom(Long id);
}
