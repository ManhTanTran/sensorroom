package com.example.sensorroom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomRequest {
    @NotBlank
    @Size(min = 2, max = 45)
    private String varchar;
}
