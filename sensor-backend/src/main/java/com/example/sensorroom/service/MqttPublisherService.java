package com.example.sensorroom.service;

import com.example.sensorroom.dto.device.DeviceCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
}

