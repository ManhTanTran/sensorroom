package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Alert.Status;
import com.example.sensorroom.request.AlertRequest;

public interface AlertService {
    Alert getAlert(Long id);

    List<Alert> getAllAlerts();

    List<Alert> getAlertsByClassroom(Long classroomId);

    List<Alert> getAlertsByResolvedStatus(Status status);

    Alert createAlert(Long classroomId, AlertRequest alertRequest);

    Alert resolveAlert(Long id); 

    void deleteAlert(Long id);
}
