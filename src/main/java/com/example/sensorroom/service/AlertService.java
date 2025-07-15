package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.Alert;

public interface AlertService {
    Alert getAlert(Long id);

    List<Alert> getAllAlerts();

    List<Alert> getAlertsByDevice(Long deviceId);

    List<Alert> getAlertsByResolvedStatus(Boolean isResolved);

    Alert createAlert(Long deviceId, Alert alert);

    Alert resolveAlert(Long id); 

    void deleteAlert(Long id);
}
