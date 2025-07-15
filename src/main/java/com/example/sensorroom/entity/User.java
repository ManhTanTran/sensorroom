package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;
}
