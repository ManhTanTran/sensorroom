package com.example.sensorroom.service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dto.classroom.*;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.exception.ResourceNotFoundException;
import com.example.sensorroom.mapper.ClassroomMapper;

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
    public ClassroomResponse create(ClassroomRequest request) {
        Classroom classroom = ClassroomMapper.toEntity(request);
        classroom.setActive(true); // default active
        Classroom saved = classroomRepository.save(classroom);
        return ClassroomMapper.toResponse(saved);
    }

    @Override
    public ClassroomResponse update(Long id, ClassroomUpdateRequest request) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + id));

        ClassroomMapper.updateEntity(classroom, request);

        Classroom updated = classroomRepository.save(classroom);
        return ClassroomMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + id));
        classroomRepository.delete(classroom);
    }

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
}
