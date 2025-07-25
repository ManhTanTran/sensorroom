package com.example.sensorroom.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sensorroom.dao.AlertRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.dto.alert.AlertRequest;
import com.example.sensorroom.dto.alert.AlertResponse;
import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Alert.Status;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.RoleType;
import com.example.sensorroom.entity.User;
import com.example.sensorroom.exception.ResourceNotFoundException;
import com.example.sensorroom.mapper.AlertMapper;
import com.example.sensorroom.service.AlertService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final DeviceRepository deviceRepository;

    private boolean isAdmin(User user) {
        return user.getAccountType() == RoleType.ADMIN;
    }

    private void checkAccess(User user, Classroom classroom) {
        if (!isAdmin(user) && (user.getClassroom() == null || !user.getClassroom().equals(classroom))) {
            throw new RuntimeException("Access denied");
        }
    }

    @Override
    public List<AlertResponse> getAll(User currentUser) {
        if (isAdmin(currentUser)) {
            return alertRepository.findAll()
                    .stream()
                    .map(AlertMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return alertRepository.findByDevice_Classroom(currentUser.getClassroom())
                .stream()
                .map(AlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AlertResponse getById(Long id, User currentUser) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        checkAccess(currentUser, alert.getDevice().getClassroom());

        return AlertMapper.toResponse(alert);
    }

    @Override
    public List<AlertResponse> getByClassroomId(Long classroomId, User currentUser) {
        Classroom classroom = currentUser.getClassroom();

        if (isAdmin(currentUser) || (classroom != null && classroom.getId().equals(classroomId))) {
            return alertRepository.findByDevice_Classroom_Id(classroomId)
                    .stream()
                    .map(AlertMapper::toResponse)
                    .collect(Collectors.toList());
        }

        throw new RuntimeException("Access denied");
    }

    @Override
    public List<AlertResponse> getByStatus(Status status, User currentUser) {
        if (isAdmin(currentUser)) {
            return alertRepository.findByIsResolved(status)
                    .stream()
                    .map(AlertMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return alertRepository.findByIsResolvedAndDevice_Classroom(status, currentUser.getClassroom())
                .stream()
                .map(AlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponse create(AlertRequest request, User currentUser) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        checkAccess(currentUser, device.getClassroom());

        Alert alert = AlertMapper.toEntity(request, device.getClassroom(), device);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setIsResolved(Status.NO);

        return AlertMapper.toResponse(alertRepository.save(alert));
    }

    @Override
    @Transactional
    public void resolveAlert(Long id, User currentUser) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        checkAccess(currentUser, alert.getDevice().getClassroom());

        alert.setIsResolved(Status.YES);
        alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void delete(Long id, User currentUser) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        checkAccess(currentUser, alert.getDevice().getClassroom());

        alertRepository.delete(alert);
    }
}
