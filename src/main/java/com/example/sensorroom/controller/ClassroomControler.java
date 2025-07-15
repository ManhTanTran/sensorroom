package com.example.sensorroom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.request.ClassroomRequest;
import com.example.sensorroom.service.ClassroomService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/classrooms")
@Tag(name = "Classroom Rest API Endpoints", description = "Operations related to classrooms")
@RequiredArgsConstructor
public class ClassroomControler {
    
    private final ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms(){
        return ResponseEntity.ok(classroomService.getAllClassroom());
    }

    @GetMapping("/id")
    public ResponseEntity<Classroom> getClassroom(@PathVariable Long id){
        return ResponseEntity.ok(classroomService.getClassroom(id));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Classroom> createClassroom(
        @PathVariable Long userId,
        @RequestBody ClassroomRequest request) {
        Classroom saved = classroomService.createClassroom(userId, request.getName());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Long id,
                                                     @Valid @RequestBody ClassroomRequest classroomRequest){
        Classroom classroom = new Classroom();
        classroom.setName(classroomRequest.getName());
        return ResponseEntity.ok(classroomService.updateClassroom(id, classroomRequest));                      
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id){
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }

}
