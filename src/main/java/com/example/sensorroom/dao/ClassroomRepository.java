package com.example.sensorroom.dao;

import com.example.sensorroom.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
	
}
