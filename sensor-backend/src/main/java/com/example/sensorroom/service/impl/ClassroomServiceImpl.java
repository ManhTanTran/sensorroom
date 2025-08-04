package com.example.sensorroom.service.impl;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dto.classroom.*;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.exception.ResourceNotFoundException;
import com.example.sensorroom.mapper.ClassroomMapper;
import com.example.sensorroom.service.ClassroomService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClassroomResponse> getAll() {
        return classroomRepository.findAll()
                .stream()
                .map(ClassroomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClassroomResponse getById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + id));
        return ClassroomMapper.toResponse(classroom);
    }

    @Override
    public Classroom createClassroom (Classroom classroom){
        return classroomRepository.save(classroom);
    }
}
