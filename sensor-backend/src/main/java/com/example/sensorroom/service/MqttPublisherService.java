package com.example.sensorroom.service;

import com.example.sensorroom.dto.device.DeviceCreateRequest;
import com.example.sensorroom.entity.constant.MqttTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttPublisherService {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publishDeviceCreate(DeviceCreateRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
                publish(MqttTopics.DEVICE_CREATE, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public void publishDeviceCreate(String deviceCode, Long classroomId) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("deviceCode", deviceCode);
        payloadMap.put("classroomId", classroomId);
        publishJson(MqttTopics.DEVICE_CREATE, payloadMap);
    }

    public void publishDeviceDelete(String deviceCode, Long classroomId) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("deviceCode", deviceCode);
        payloadMap.put("classroomId", classroomId);
        publishJson(MqttTopics.DEVICE_DELETE, payloadMap);
    }

    private void publishJson(String topic, Map<String, Object> payloadMap) {
        try {
            String payload = objectMapper.writeValueAsString(payloadMap);
            publish(topic, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publish(String topic, String payload) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(1); // có thể chỉnh nếu cần
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

