package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Classroom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
