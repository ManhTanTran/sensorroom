package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.DeviceData;

public interface DeviceDataService {
    
    DeviceData getData(Long id);

    List<DeviceData> getAllData();

    List<DeviceData> getDataByDevice(Long deviceId);

    DeviceData createData(Long deviceId, DeviceData data);
}
