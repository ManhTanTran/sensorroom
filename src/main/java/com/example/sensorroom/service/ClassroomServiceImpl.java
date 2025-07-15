package com.example.sensorroom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.UserRepository;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.User;
import com.example.sensorroom.request.ClassroomRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomServiceImpl implements ClassroomService{

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    @Override 
    public Classroom getClassroom(Long id){
        return classroomRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
    }

    @Override
    public List<Classroom> getAllClassroom(){
        return classroomRepository.findAll();
    }

    @Override
    public List<Classroom> getClassroomsByUser(Long userId) {
        return classroomRepository.findAll()
                .stream()
                .filter(c -> c.getUser().getId().equals(userId))
                .toList();
    }

    @Override 
     public Classroom createClassroom(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Classroom classroom = new Classroom();
        classroom.setName(name);
        classroom.setUser(user);
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
