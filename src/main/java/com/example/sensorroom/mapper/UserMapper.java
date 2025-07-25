package com.example.sensorroom.mapper;

import com.example.sensorroom.dto.user.UserRequest;
import com.example.sensorroom.dto.user.UserResponse;
import com.example.sensorroom.dto.user.UserUpdateRequest;
import com.example.sensorroom.entity.User;


public class UserMapper {
    public static User toEntity(UserRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword()) // Lưu ý: nên mã hoá
                .hometown(request.getHometown())
                .accountType(request.getAccountType())
                .build();
    }

    public static UserResponse toResponse(User user) {
    return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .hometown(user.getHometown())
            .accountType(user.getAccountType())
            .createdAt(user.getCreatedAt())
            .classroomId(user.getClassroom() != null ? user.getClassroom().getId() : null)
            .build();
}

    public static void updateEntity(User user, UserUpdateRequest request) {
        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        if (request.getHometown() != null)
            user.setHometown(request.getHometown());

        if (request.getAccountType() != null)
            user.setAccountType(request.getAccountType());
    }
}
