package com.example.sensorroom.service;

import com.example.sensorroom.dao.ClassroomRepository;
import com.example.sensorroom.dao.DeviceDataRepository;
import com.example.sensorroom.dao.DeviceRepository;
import com.example.sensorroom.dto.devicedata.DeviceDataRequest;
import com.example.sensorroom.entity.DeviceData;
import com.example.sensorroom.entity.DeviceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttSensorDataListener {

    private MqttClient client;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient("tcp://broker.hivemq.com:1883", MqttClient.generateClientId());
            client.connect();

            client.subscribe("/virtual-sensor/#", (topic, message) -> {
                try {
                    String payload = new String(message.getPayload());
                    System.out.println("MQTT received [" + topic + "]: " + payload);

                    // Parse JSON
                    DeviceDataRequest req = objectMapper.readValue(payload, DeviceDataRequest.class);

                    var deviceOpt = deviceRepository.findByDeviceCode(req.getDeviceCode());
                    var classroomOpt = classroomRepository.findById(req.getClassroomId());

                    if (deviceOpt.isEmpty() || classroomOpt.isEmpty()) {
                        System.err.println("❌ Device or Classroom not found!");
                        return;
                    }

                    var device = deviceOpt.get();

                    // ✅ Kiểm tra trạng thái thiết bị
                    if (device.getStatus() != DeviceStatus.ACTIVE) {
                        System.out.println("⚠️ Device " + device.getDeviceCode() + " is not ACTIVE. Skipping data.");
                        return;
                    }

                    DeviceData data = DeviceData.builder()
                            .temperature(req.getTemperature())
                            .humidity(req.getHumidity())
                            .light(req.getLight())
                            .co2(req.getCo2())
                            .device(device)
                            .classroom(classroomOpt.get())
                            .build();

                    deviceDataRepository.save(data);
                    System.out.println("✅ Data saved for device: " + device.getDeviceCode());

                } catch (Exception e) {
                    System.err.println("❌ MQTT parse/save failed: " + e.getMessage());
                }
            });

        } catch (MqttException e) {
            System.err.println("❌ MQTT connect failed: " + e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                client.close();
                System.out.println("MQTT client disconnected.");
            } catch (MqttException e) {
                System.err.println("❌ Failed to disconnect MQTT client: " + e.getMessage());
            }
        }
    }
}
