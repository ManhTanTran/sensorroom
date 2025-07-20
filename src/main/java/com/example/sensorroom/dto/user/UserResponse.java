package com.example.sensorroom.dto.user;

import java.util.Date;

import com.example.sensorroom.entity.RoleType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String hometown;
    private RoleType accountType;
    private Date createdAt;
    private Long classroomId;

}

