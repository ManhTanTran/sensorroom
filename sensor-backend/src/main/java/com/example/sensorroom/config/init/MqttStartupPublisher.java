// package com.example.sensorroom.config.init;

// import com.example.sensorroom.dto.device.DeviceCreateRequest;
// import com.example.sensorroom.service.MqttPublisherService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// public class MqttStartupPublisher {

//     private final MqttPublisherService mqttPublisherService;

//     @EventListener(ApplicationReadyEvent.class)
//     public void sendOnStartup() {
//         mqttPublisherService.publishDeviceCreate(
//             new DeviceCreateRequest("DV-201A-01", 3L)
//         );
//     }
// }
