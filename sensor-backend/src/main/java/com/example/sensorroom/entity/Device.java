package com.example.sensorroom.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.sensorroom.entity.constant.DeviceStatus;
import com.example.sensorroom.entity.constant.DeviceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "device_name", nullable = false)
    private String name;

    @Column(name = "device_code", nullable = false)
    private String deviceCode;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Column(nullable = false)
    private Integer dataCycle; 

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceData> deviceDataList;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }



}
