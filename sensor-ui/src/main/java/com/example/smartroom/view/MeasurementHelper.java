package com.example.smartroom.view;

import com.example.smartroom.model.ClassroomMeasurement;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.DeviceData;
import com.example.smartroom.service.DataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MeasurementHelper {

    public static List<ClassroomMeasurement> getRecentRoomMeasurements(String roomId) {
        List<Device> allDevices = DataService.getAllDevices();
        List<DeviceData> allData = DataService.getAllDeviceData();
        System.out.println("📊 Tổng số bản ghi data: " + allData.size());
        for (int i = 0; i < Math.min(10, allData.size()); i++) {
            DeviceData d = allData.get(i);
            System.out.println("📦 DeviceData: id=" + d.getDeviceId() + ", time=" + d.getCreatedAt());
        }

        System.out.println("▶ Room ID yêu cầu: " + roomId);

        // Tìm deviceId của các thiết bị trong phòng (dùng trim và ignoreCase để tránh lỗi)
        Set<String> deviceIdsInRoom = allDevices.stream()
                .filter(d -> {
                    boolean match = roomId.trim().equalsIgnoreCase(d.getRoom().trim());
                    if (match) {
                        System.out.println("✔ Match device: " + d.getDeviceId() + " - Room: " + d.getRoom());
                    }
                    return match;
                })
                .map(Device::getDeviceId)
                .collect(Collectors.toSet());

        if (deviceIdsInRoom.isEmpty()) {
            System.out.println("⚠ Không tìm thấy thiết bị nào cho phòng: " + roomId);
        }

        // Lọc device data của phòng đó
        List<DeviceData> roomData = allData.stream()
                .filter(d -> deviceIdsInRoom.contains(d.getDeviceId()) && d.getCreatedAt() != null)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // mới nhất trước
                .toList();

        System.out.println("▶ Số bản ghi data được lọc: " + roomData.size());

        // Gom các lần đo theo thời gian
        Map<String, Map<String, String>> timeToMeasurements = new LinkedHashMap<>();

        for (DeviceData d : roomData) {
            String time = d.getCreatedAt();
            timeToMeasurements.putIfAbsent(time, new HashMap<>());
            Map<String, String> map = timeToMeasurements.get(time);

            if (d.getTemperature() != null) map.put("temperature", d.getTemperature().toString());
            if (d.getHumidity() != null) map.put("humidity", d.getHumidity().toString());
            if (d.getLight() != null) map.put("lux", d.getLight().toString());
            if (d.getCo2() != null) map.put("co2", String.format("%.2f", d.getCo2()));
        }

        List<ClassroomMeasurement> result = new ArrayList<>();
        int count = 0;

        for (Map.Entry<String, Map<String, String>> entry : timeToMeasurements.entrySet()) {
            if (count >= 3) break;

            Map<String, String> m = entry.getValue();
            String formattedTime = formatTime(entry.getKey());

            ClassroomMeasurement cm = new ClassroomMeasurement(
                    m.getOrDefault("humidity", "-"),
                    m.getOrDefault("temperature", "-"),
                    m.getOrDefault("lux", "-"),
                    m.getOrDefault("co2", "-"),
                    formattedTime,
                    getQuality(m)
            );

            System.out.println("→ Measurement #" + (count + 1) + ": " + formattedTime + " | " +
                    cm.temperatureProperty().get() + "°C, " +
                    cm.humidityProperty().get() + "%, " +
                    cm.luxProperty().get() + " lux, " +
                    cm.co2Property().get() + " ppm");

            result.add(cm);
            count++;
        }

        return result;
    }


    private static String formatTime(String raw) {
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            return raw;
        }
    }

    private static String getQuality(Map<String, String> m) {
            try {
                double temp = Double.parseDouble(m.getOrDefault("temperature", "-1"));
                double hum = Double.parseDouble(m.getOrDefault("humidity", "-1"));
                double co2 = Double.parseDouble(m.getOrDefault("co2", "-1"));
                double lux = Double.parseDouble(m.getOrDefault("lux", "-1"));

                boolean tempOk = temp >= 18 && temp <= 30;
                boolean humOk = hum >= 35 && hum <= 70;
                boolean co2Ok = co2 > 0 && co2 <= 1500;
                boolean luxOk = lux >= 200 && lux <= 1500;

                if (tempOk && humOk && co2Ok && luxOk) {
                    return "Tốt";
                }

                if (!tempOk || !humOk || !co2Ok || !luxOk) {
                    return "Kém";
                }

                return "Khá";
            } catch (Exception e) {
                return "Không rõ";
            }
    }
}
