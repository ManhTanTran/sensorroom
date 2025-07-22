package com.example.sensorroom.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.dto.device.DeviceResponse;
import com.example.sensorroom.exception.ResourceNotFoundException;
import com.example.sensorroom.mapper.DeviceMapper;
import com.example.sensorroom.service.DeviceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;


    @Override
    public DeviceResponse getById(Long id) {
        return DeviceMapper.toResponse(deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found")));
    }

    @Override
    public List<DeviceResponse> getAll() {
        return deviceRepository.findAll()
                .stream()
                .map(DeviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    
}

