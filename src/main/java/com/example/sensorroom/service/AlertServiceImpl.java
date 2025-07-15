package com.example.sensorroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.AlertRepository;
import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Alert.Status;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.request.AlertRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertServiceImpl implements AlertService {
    
    private final AlertRepository alertRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public Alert getAlert(Long id){
        return alertRepository.findById(id)
            .orElseThrow(()-> new EntityNotFoundException("Alert not found"));
    }

    @Override 
    public List<Alert> getAllAlerts(){
        return alertRepository.findAll();
    }

    @Override
    public List<Alert> getAlertsByClassroom(Long classroomId){
        return alertRepository.findAll()
                .stream()
                .filter(a -> a.getClassroom().getId().equals(classroomId))
                .toList();
    }

    
    @Override
    public List<Alert> getAlertsByResolvedStatus(Status status) {
        return alertRepository.findAll()
                .stream()
                .filter(a -> a.getIsResolved() == status)
                .toList();
    }

    @Override
    public Alert createAlert(Long classroomId, AlertRequest request) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Alert alert = new Alert();
        alert.setAlertType(request.getAlertType());
        alert.setMessage(request.getMessage());
        alert.setCreatedAt(LocalDateTime.now());
        alert.setIsResolved(Alert.Status.NO);
        alert.setClassroom(classroom);

        return alertRepository.save(alert);
    }

    @Override
    public Alert resolveAlert(Long id) {
        Alert alert = getAlert(id);
        alert.setIsResolved(Alert.Status.YES);
        return alertRepository.save(alert);
    }

    @Override
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}
