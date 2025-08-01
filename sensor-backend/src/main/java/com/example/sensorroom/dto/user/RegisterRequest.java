package com.example.sensorroom.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    
    @NotEmpty(message = "Full name is mandatory")
    @Size(min = 3, max = 30, message = "Full name must be at least 3 characters long")
    private String fullName;

    @NotEmpty(message = "Username is mandatory")
    @Size(min = 3, max = 30, message = "Username must be at least 3 characters long")
    private String userName;

    @NotEmpty(message = "Password is mandatory")
    @Size(min = 5, max = 30, message = "Password must be at least 3 characters long")
    private String password;
}
