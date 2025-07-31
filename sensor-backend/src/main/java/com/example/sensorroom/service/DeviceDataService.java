package com.example.sensorroom.service;

import java.util.List;

import com.example.sensorroom.dto.devicedata.DeviceDataRequest;
import com.example.sensorroom.dto.devicedata.DeviceDataResponse;


public interface DeviceDataService {

    List<DeviceDataResponse> getAll();

    List<DeviceDataResponse> getByDeviceId(String deviceId);

    DeviceDataResponse create(DeviceDataRequest request);

}
