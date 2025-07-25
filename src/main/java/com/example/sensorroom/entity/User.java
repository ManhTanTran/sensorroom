package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    private String username;

    private String password;

    private String hometown;


    @Enumerated(EnumType.STRING)
    private RoleType accountType;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @OneToMany(mappedBy = "createdBy")
    private List<Device> createdDevices;


    
}
