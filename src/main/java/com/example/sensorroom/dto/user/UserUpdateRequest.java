package com.example.sensorroom.dto.user;

import com.example.sensorroom.entity.RoleType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String fullName;
    private String hometown;
    private RoleType accountType;
    private Long classroomId;
}
