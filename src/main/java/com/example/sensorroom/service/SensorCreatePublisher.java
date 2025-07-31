package com.example.sensorroom.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SensorCreatePublisher {
    public static void main(String[] args) {
        String broker = "tcp://broker.hivemq.com:1883"; // hoặc mqtt://localhost:1883 nếu chạy local
        String clientId = "java-mqtt-client-" + System.currentTimeMillis();
        String topic = "/sensors/create";

        String payload = """
        {
            "deviceId": "3",
            "classroomId": "4",
        }
        """;

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options);

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);

            client.publish(topic, message);
            System.out.println("✅ Published to " + topic);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}