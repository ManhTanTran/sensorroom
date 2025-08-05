package com.example.sensorroom.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientConfig {

    @Bean
    public MqttClient mqttClient() {
        String brokerUrl = "tcp://broker.hivemq.com:1883";
        String clientId = "sensorroom-publisher-" + System.currentTimeMillis();

        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            client.connect();
            System.out.println("✅ MQTT connected to broker");
            return client;
        } catch (MqttException e) {
            System.err.println("❌ Failed to connect to MQTT broker: " + e.getMessage());
            throw new RuntimeException("MQTT connection failed", e);
        }
    }
}
