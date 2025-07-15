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

    private String dataType;

    private Double value;

    private LocalDateTime recordedAt;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
