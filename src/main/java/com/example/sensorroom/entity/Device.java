package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Device")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SensorType type;

    private String status;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom clasroom;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceData> deviceDatas;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Alert> alerts;
}
