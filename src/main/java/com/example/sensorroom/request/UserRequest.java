package com.example.sensorroom.request;

import jakarta.validation.constraints.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    
    @NotBlank
    @Size(min = 2, max = 45)
    private String name;

    @NotBlank
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank
    private String role;


}
