package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.request.DeviceDataRequest;

public interface DeviceDataService {
    
    DeviceData getData(Long id);

    List<DeviceData> getAllData();

    List<DeviceData> getDataByClassroom(Long classroomId);

    DeviceData createData(Long classroomId, DeviceDataRequest request);
}
