package com.example.sensorroom.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sensorroom.dao.DeviceDataRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.entity.Device;
import com.example.sensorroom.entity.DeviceData;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceDataServiceImpl implements DeviceDataService {

    private final DeviceDataRepository deviceDataRepository;
    private final DeviceRepository deviceRepository;

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
    public List<DeviceData> getDataByDevice(Long deviceId) {
        return deviceDataRepository.findByDeviceId(deviceId);
    }

    @Override
    public DeviceData createData(Long deviceId, DeviceData data) {
        Device device = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        data.setDevice(device);
        data.setRecordedAt(LocalDateTime.now());
        return deviceDataRepository.save(data);
    }
}
