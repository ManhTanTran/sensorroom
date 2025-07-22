package com.example.sensorroom.controller;

import com.example.sensorroom.dto.classroom.*;
import com.example.sensorroom.service.ClassroomService;

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
    public ResponseEntity<ClassroomResponse> getClassroom(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

}
    
