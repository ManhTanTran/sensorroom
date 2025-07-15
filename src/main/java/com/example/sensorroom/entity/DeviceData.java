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

    private String data_type;

    private Double value;

    private LocalDateTime recordedAt;

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
