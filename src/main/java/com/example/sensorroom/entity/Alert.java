package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String alertType;
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status isResolved = Status.NO;

    private LocalDateTime createdAt;

    public enum Status {
        YES,
        NO
    }

    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;
}
