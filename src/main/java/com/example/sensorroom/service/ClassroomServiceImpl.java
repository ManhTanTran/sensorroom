package com.example.sensorroom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.request.ClassroomRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomServiceImpl implements ClassroomService{

    private final ClassroomRepository classroomRepository;

    @Override 
    public Classroom getClasroom(Long id){
        return classroomRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
    }

    @Override
    public List<Classroom> getAllClassroom(){
        return classroomRepository.findAll();
    }

    @Override 
    public Classroom createClassroom (Classroom classroom){
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom updateClassroom(Long id, ClassroomRequest classroomRequest) {
    Classroom existing = classroomRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        existing.setName(classroomRequest.getName());
        return classroomRepository.save(existing);
    }
    
    @Override
    public void deleteClassroom (Long id){
        classroomRepository.deleteById(id);
    }
}
