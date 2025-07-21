package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.dto.alert.AlertRequest;
import com.example.sensorroom.dto.alert.AlertResponse;
import com.example.sensorroom.entity.Alert.Status;
import com.example.sensorroom.entity.User;

public interface AlertService {

    List<AlertResponse> getAll(User currentUser);

    AlertResponse getById(Long id, User currentUser);

    List<AlertResponse> getByClassroomId(Long classroomId, User currentUser);

    List<AlertResponse> getByStatus(Status status, User currentUser);

    AlertResponse create(AlertRequest request, User currentUser);

    void resolveAlert(Long id, User currentUser);

    void delete(Long id, User currentUser);
}
