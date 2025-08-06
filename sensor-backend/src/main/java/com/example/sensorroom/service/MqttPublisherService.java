package com.example.sensorroom.service;

import com.example.sensorroom.dto.device.DeviceCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttPublisherService {

    private final MqttClient mqttClient;

    public void publishDeviceCreate(DeviceCreateRequest request) {
        try {
            String topic = "/sensors/create";
            String payload = new ObjectMapper().writeValueAsString(request);
            mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public void publishDeviceControl(String deviceCode, Long classroomId) {
        try {
            String topic = "/sensors/create";

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("deviceCode", deviceCode);
            payloadMap.put("classroomId", classroomId);

            String payload = new ObjectMapper().writeValueAsString(payloadMap);
            mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

