package com.example.sensorroom.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final DeviceRepository deviceRepository;
    private final UserService userService;

    private boolean isAdmin(User user) {
        return user.getAccountType() == RoleType.ADMIN;
    }

    private void checkAccess(User user, Classroom classroom) {
        if (!isAdmin(user) && !classroom.equals(user.getClassroom())) {
            throw new AccessDeniedException("You do not have permission for this classroom");
        }
    }

    @Override
    public List<AlertResponse> getAll() {
        User user = userService.getCurrentUser();

        if (isAdmin(user)) {
            return alertRepository.findAll()
                    .stream()
                    .map(AlertMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return alertRepository.findByDevice_Classroom(user.getClassroom())
                .stream()
                .map(AlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AlertResponse getById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        User user = userService.getCurrentUser();
        checkAccess(user, alert.getDevice().getClassroom());

        return AlertMapper.toResponse(alert);
    }

    @Override
    public List<AlertResponse> getByClassroomId(Long classroomId) {
        User user = userService.getCurrentUser();

        Classroom classroom = user.getClassroom();
        if (isAdmin(user) || (classroom != null && classroom.getId().equals(classroomId))) {
            return alertRepository.findByDevice_Classroom_Id(classroomId)
                    .stream()
                    .map(AlertMapper::toResponse)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException("You do not have access to this classroom");
    }

    @Override
    public List<AlertResponse> getByStatus(Status status) {
        User user = userService.getCurrentUser();

        if (isAdmin(user)) {
            return alertRepository.findByIsResolved(status)
                    .stream()
                    .map(AlertMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return alertRepository.findByIsResolvedAndDevice_Classroom(status, user.getClassroom())
                .stream()
                .map(AlertMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponse create(AlertRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        User user = userService.getCurrentUser();
        checkAccess(user, device.getClassroom());

        Alert alert = AlertMapper.toEntity(request, device.getClassroom(), device);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setIsResolved(Status.NO);

        return AlertMapper.toResponse(alertRepository.save(alert));
    }

    @Override
    @Transactional
    public void resolveAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        User user = userService.getCurrentUser();
        checkAccess(user, alert.getDevice().getClassroom());

        alert.setIsResolved(Status.YES);
        alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        User user = userService.getCurrentUser();
        checkAccess(user, alert.getDevice().getClassroom());

        alertRepository.delete(alert);
    }
}
