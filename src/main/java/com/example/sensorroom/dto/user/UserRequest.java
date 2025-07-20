package com.example.sensorroom.dto.user;

import com.example.sensorroom.entity.RoleType;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private String hometown;

    @NotNull
    private RoleType accountType;

    @NotNull
    private Long classroomId;
}
