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

    private String alert_type; 
    private String message;
    private boolean is_resolved;

    private LocalDateTime recordedAt;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.recordedAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return recordedAt;
    }

    public Boolean getIsResolved() {
        return is_resolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.is_resolved = isResolved;
    }

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
