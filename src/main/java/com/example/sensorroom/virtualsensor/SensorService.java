package com.example.sensorroom.virtualsensor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.example.sensorroom.dto.SensorData;
import com.example.sensorroom.dto.SensorResponse;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
public class SensorService {
    private static final String BACKEND_URL = "http://localhost:8080/api/sensor-data";
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public void sendSensorData() {
        SensorData data = generateSensorData(1); // Hardcoded classroomId = 1 for now
        String json = GSON.toJson(data);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BACKEND_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // Optional: parse response
            SensorResponse parsed = GSON.fromJson(response.body(), SensorResponse.class);
            System.out.println("Backend says: " + parsed.getStatus() + " - " + parsed.getMessage());

        } catch (Exception e) {
            System.err.println("Failed to send sensor data: " + e.getMessage());
        }
    }

    private SensorData generateSensorData(int classroomId) {
        Random random = new Random();
        double temperature = 20 + random.nextDouble() * 10; // 20 - 30
        double humidity = 40 + random.nextDouble() * 30;    // 40 - 70
        double co2 = 400 + random.nextDouble() * 600;       // 400 - 1000

        return new SensorData(classroomId, temperature, humidity, co2);
    }

}
