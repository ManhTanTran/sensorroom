package com.example.sensorroom.controller;

import com.example.sensorroom.dto.classroom.*;
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

    // 1. Lấy danh sách phòng học (có phân quyền ở tầng security)
    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAll());
    }

    // 2. Lấy thông tin 1 phòng học theo id
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getClassroom(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    // 3. Tạo phòng học mới
    @PostMapping
    public ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.ok(classroomService.create(request));
    }

    // 4. Cập nhật thông tin phòng học
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResponse> updateClassroom(@PathVariable Long id,
                                                              @Valid @RequestBody ClassroomUpdateRequest request) {
        return ResponseEntity.ok(classroomService.update(id, request));
    }

    // 5. Xoá phòng học (hiện tại là xoá mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
