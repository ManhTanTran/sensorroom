package com.example.sensorroom.controller;

import com.example.sensorroom.dto.classroom.*;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.service.ClassroomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getClassroom(@PathVariable("id") Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@Valid @RequestBody ClassroomRequest classroomRequest){
        Classroom classroom = new Classroom();
        classroom.setName(classroomRequest.getName());
        return ResponseEntity.ok(classroomService.createClassroom(classroom));
    }


}
    
