package com.example.sensorroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.AlertRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Device;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertServiceImpl implements AlertService {
    
    private final AlertRepository alertRepository;
    private final DeviceRepository deviceRepository;

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
    public List<Alert> getAlertsByDevice(Long deviceId){
        return alertRepository.findByDeviceId(deviceId);
    }

    @Override
    public List<Alert> getAlertsByResolvedStatus(Boolean isResolved){
        return alertRepository.findByIsResolved(isResolved);
    }

    @Override
    public Alert createAlert(Long deviceId, Alert alert) {
        Device device = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        alert.setDevice(device);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setIsResolved(alert.getIsResolved() != null ? alert.getIsResolved() : false);
        return alertRepository.save(alert);
    }

    @Override
    public Alert resolveAlert(Long id) {
        Alert alert = getAlert(id);
        alert.setIsResolved(true);
        return alertRepository.save(alert);
    }

    @Override
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}
