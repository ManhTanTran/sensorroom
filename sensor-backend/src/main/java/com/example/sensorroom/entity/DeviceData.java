package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "devicedatas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperature;
    private Double humidity;
    private Double light;
    private Double co2;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_code", referencedColumnName = "device_code", nullable = false)
    private Device device;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
