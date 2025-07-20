package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.dto.alert.AlertRequest;
import com.example.sensorroom.dto.alert.AlertResponse;
import com.example.sensorroom.entity.Alert.Status;


public interface AlertService {
    List<AlertResponse> getAll(); 
    AlertResponse getById(Long id); 
    List<AlertResponse> getByClassroomId(Long classroomId); // USER hoặc ADMIN
    List<AlertResponse> getByStatus(Status status); 

    AlertResponse create(AlertRequest request); 
    void resolveAlert(Long id); 
    void delete(Long id); 
}
