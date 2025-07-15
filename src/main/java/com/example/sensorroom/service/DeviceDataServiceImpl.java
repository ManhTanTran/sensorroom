package com.example.sensorroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.DeviceDataRepository;
import com.example.sensorroom.dao.AlertRepository;
import com.example.sensorroom.entity.Alert;
import com.example.sensorroom.entity.Classroom;
import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.request.DeviceDataRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceDataServiceImpl implements DeviceDataService {
    private final DeviceDataRepository deviceDataRepository;
    private final ClassroomRepository classroomRepository;
    private final AlertRepository alertRepository;

    @Override
    public DeviceData getData(Long id) {
        return deviceDataRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Data not found"));
    }

    @Override
    public List<DeviceData> getAllData() {
        return deviceDataRepository.findAll();
    }

     @Override
    public List<DeviceData> getDataByClassroom(Long classroomId) {
        return deviceDataRepository.findAll()
                .stream()
                .filter(d -> d.getClassroom().getId().equals(classroomId))
                .toList();
    }

    @Override
    @Transactional
    public DeviceData createData(Long classroomId, DeviceDataRequest request) {
    Classroom classroom = classroomRepository.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

    DeviceData data = new DeviceData();
    data.setTemperature(request.getTemperature());
    data.setHumidity(request.getHumidity());
    data.setCo2(request.getCo2());
    data.setCreatedAt(LocalDateTime.now());
    data.setClassroom(classroom);

    DeviceData savedData = deviceDataRepository.save(data);

    checkForAlerts(savedData, classroom);

    return savedData;
}


    private void checkForAlerts(DeviceData data, Classroom classroom) {
        if (data.getTemperature() != null && data.getTemperature() > 30) {
            createAlert("TEMP_HIGH", "Temperature exceeded 30°C", classroom);
        }
        if (data.getCo2() != null && data.getCo2() > 1000) {
            createAlert("CO2_HIGH", "CO2 exceeded 1000 ppm", classroom);
        }
    }

    private void createAlert(String type, String message, Classroom classroom) {
        Alert alert = new Alert();
        alert.setAlertType(type);
        alert.setMessage(message);
        alert.setIsResolved(Alert.Status.NO);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setClassroom(classroom);
        alertRepository.save(alert);
    }
}
