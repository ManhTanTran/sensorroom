package com.example.sensorroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    private List<Device> devices;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    private List<User> users;

}
