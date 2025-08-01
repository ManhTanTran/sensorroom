package com.example.sensorroom.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientConfig {

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String brokerUrl = "tcp://broker.hivemq.com:1883"; // hoặc URL broker bạn dùng
        String clientId = "sensorroom-publisher-" + System.currentTimeMillis();

        MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        client.connect(); // kết nối luôn
        return client;
    }
}
