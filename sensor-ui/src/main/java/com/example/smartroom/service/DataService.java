package com.example.smartroom.service;

import com.example.smartroom.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataService {

    private static final ApiService apiService = new ApiService();

    private static final ObservableList<Device> allDevices = FXCollections.observableArrayList(
            device -> new Observable[] {device.roomProperty(), device.statusProperty()}
    );

    private static final ObservableList<Classroom> allClassrooms = FXCollections.observableArrayList();

    private static ObservableList<AlertHistory> generatedAlerts = FXCollections.observableArrayList();

    private static final ObservableList<DeviceData> allDeviceData = FXCollections.observableArrayList();


    public static ObservableList<DeviceData> getAllDeviceData() {
        // Nếu bạn đã có API để lấy device data, hãy gọi nó tại đây
        if (allDeviceData.isEmpty()) {
            loadAllDeviceDataFromApi();
            System.out.println("📋 Danh sách thiết bị:");
            getAllDevices().forEach(dev ->
                    System.out.println(dev.getDeviceId() + " - " + dev.getType() + " - room: " + dev.getRoom())
            );// hoặc async nếu cần
        }
        return allDeviceData;
    }

    public static CompletableFuture<ObservableList<Classroom>> fetchClassroomsWithDevices() {
        CompletableFuture<ObservableList<Classroom>> classroomsFuture = apiService.fetchClassrooms();
        CompletableFuture<ObservableList<Device>> devicesFuture = apiService.fetchDevices();

        return CompletableFuture.allOf(classroomsFuture, devicesFuture)
                .thenApply(ignored -> {
                    ObservableList<Classroom> classrooms = classroomsFuture.join();
                    ObservableList<Device> devices = devicesFuture.join();

                    for (Classroom classroom : classrooms) {
                        List<Device> devicesInThisRoom = devices.stream()
                                .filter(device -> {
                                    String roomFromDevice = device.getRoom();
                                    String roomFromClass = classroom.getRoomNumber();
                                    return roomFromDevice != null && roomFromDevice.equals(roomFromClass);
                                })
                                .toList();
                        classroom.setDevicesInRoom(devicesInThisRoom);
                    }

                    return classrooms;
                });
    }

    private static final String BASE_URL = "http://localhost:8080/api";

    public static ObservableList<Classroom> getAllClassroomsFromApi() {
        ObservableList<Classroom> classrooms = FXCollections.observableArrayList();
        try {
            URL url = new URL(BASE_URL + "/classrooms");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseText.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Classroom>>() {}.getType();
                List<Classroom> classroomList = gson.fromJson(responseText.toString(), listType);

                // ⚠️ Fetch thiết bị đồng bộ
                ObservableList<Device> devices = apiService.fetchDevices().join();

                for (Classroom classroom : classroomList) {
                    // ✅ Ghép thiết bị dựa trên roomNumber
                    List<Device> devicesInThisRoom = devices.stream()
                            .filter(device -> {
                                String roomFromDevice = device.getRoom();
                                String roomFromClass = classroom.getRoomNumber();
                                return roomFromDevice != null && roomFromDevice.equals(roomFromClass);
                            })
                            .toList();

                    classroom.setDevicesInRoom(devicesInThisRoom);
                    classroom.postProcess(); // ✅ Update chỉ số (temp, humidity,...)

                    // Debug:
                    System.out.println("Phòng " + classroom.getRoomNumber() + " có " + devicesInThisRoom.size() + " thiết bị.");
                }

                classrooms.setAll(classroomList);

            } else {
                System.err.println("Lỗi API classroom: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    public static void loadAllDevicesFromApi() {
        ObservableList<Device> devices = apiService.fetchDevices().join();
        allDevices.setAll(devices);
    }


    // Tùy chọn: thêm method load data từ API:
    public static void loadAllDeviceDataFromApi() {
        try {
            URL url = new URL(BASE_URL + "/device-data"); // hoặc endpoint phù hợp
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseText.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<DeviceData>>() {}.getType();
                List<DeviceData> dataList = gson.fromJson(responseText.toString(), listType);
                allDeviceData.setAll(dataList);

            } else {
                System.err.println("Lỗi API DeviceData: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Classroom> getClassroomsForKtvFromApi(List<String> managedIds) {
        ObservableList<Classroom> all = getAllClassroomsFromApi();

        return all.filtered(classroom -> managedIds.contains(classroom.getId()));
    }


    public enum AirQuality { TỐT, KHÁ, KÉM }

    public static AirQuality getAirQuality(Classroom classroom) {
        double averageScore = getAirQualityScore(classroom);
        if (averageScore >= 4) return AirQuality.TỐT;
        if (averageScore >= 2.5) return AirQuality.KHÁ;
        return AirQuality.KÉM;
    }

    private static double getAirQualityScore(Classroom classroom) {
        int totalScore = 0;
        int count = 0;

        double temp = classroom.getTemperature();
        if (temp > 0) {  // chỉ tính nếu có dữ liệu
            count++;
            if (temp >= 22 && temp <= 27) totalScore += 5;
            else if ((temp >= 18 && temp < 22) || (temp > 27 && temp <= 30)) totalScore += 3;
        }

        double humidity = classroom.getHumidity();
        if (humidity > 0) {
            count++;
            if (humidity >= 45 && humidity <= 60) totalScore += 5;
            else if ((humidity >= 35 && humidity < 45) || (humidity > 60 && humidity <= 70)) totalScore += 3;
        }

        double lux = classroom.getLux();
        if (lux > 0) {
            count++;
            if (lux >= 300 && lux <= 500) totalScore += 5;
            else if ((lux >= 200 && lux < 300) || (lux > 500 && lux <= 1500)) totalScore += 3;
        }

        double co2 = classroom.getCo2();
        if (co2 > 0) {
            count++;
            if (co2 <= 1000) totalScore += 5;
            else if (co2 <= 1500) totalScore += 3;
        }

        if (count == 0) return 0; // tránh chia 0
        return totalScore / (double) count;
    }

    public static boolean isAnyReadingOverThreshold(Classroom classroom) {
        double temp = classroom.getTemperature();
        // Nhiệt độ quá lạnh (< 18) hoặc quá nóng (> 30)
        if (temp < 18 || temp > 30) return true;

        double humidity = classroom.getHumidity();
        // Độ ẩm quá khô (< 35) hoặc quá ẩm (> 70)
        if (humidity < 35 || humidity > 70) return true;

        double lux = classroom.getLux();
        // Ánh sáng quá tối (< 200) hoặc quá chói (> 1500)
        if (lux < 200 || lux > 1500) return true;

        double co2 = classroom.getCo2();
        // CO2 ở mức có hại (> 1500)
        if (co2 > 1500) return true;

        // Nếu tất cả các chỉ số đều trong ngưỡng chấp nhận được
        return false;
    }
    public static ObservableList<PieChart.Data> getRoomQualityDistribution(ObservableList<Classroom> classrooms) {
        Map<AirQuality, Long> qualityCounts = classrooms.stream().collect(Collectors.groupingBy(DataService::getAirQuality, Collectors.counting()));
        return qualityCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey().toString().replace("_", " "), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<Device> getAllDevices() { return allDevices; }
    public static ObservableList<Classroom> getAllClassrooms() { return allClassrooms; }
    public static void setAllClassrooms(List<Classroom> classrooms) {
        allClassrooms.setAll(classrooms);
    }
    public static void setAllDevices(List<Device> devices) {
        allDevices.setAll(devices);
    }
    public static void addClassroom(Classroom classroom) { allClassrooms.add(classroom); }
    public static List<Device> getDevicesByRoomId(String roomId) { return getAllDevices().stream().filter(device -> roomId.equals(device.getRoom())).collect(Collectors.toList()); }
    public static ObservableList<Device> getDevicesForKtv(List<Long> managedRoomIds) {
        ApiService api = new ApiService();
        ObservableList<Device> allDevices = FXCollections.observableArrayList();

        for (Long roomId : managedRoomIds) {
            List<Device> devices = api.fetchDevicesByClassroomIdSync(roomId);
            allDevices.addAll(devices);
        }

        return allDevices;
    }

    /**
     * SỬA LỖI LOGIC TẠI ĐÂY
     */
    public static ObservableList<Classroom> getClassroomsForKtv(List<String> managedRooms) {
        return allClassrooms.stream()
                .filter(classroom -> managedRooms.contains(classroom.getId()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    public static ObservableList<PieChart.Data> getSensorTypeData(ObservableList<Device> devices) { Map<String, Long> typeCounts = devices.stream().collect(Collectors.groupingBy(Device::getType, Collectors.counting())); return typeCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey(), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList)); }
    //public static ObservableList<AlertHistory> getAlertHistory() { return alertHistory; }
    public static ObservableList<XYChart.Data<Number, String>> getTopQualityRooms(ObservableList<Classroom> classrooms) {
        return classrooms.stream()
                .filter(c -> "ACTIVE".equals(c.statusProperty().get()))
                .sorted(Comparator.comparingDouble(DataService::getAirQualityScore).reversed())
                .limit(5)
                .map(c -> new XYChart.Data<>((Number)(getAirQualityScore(c) * 20), c.getRoomNumber()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static void regenerateAlertsFromDeviceData() {
        Platform.runLater(() -> {
            List<DeviceData> dataList = new ArrayList<>(getAllDeviceData()).stream()
                    .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().length() >= 16)
                    .filter(d -> {
                        try {
                            LocalDateTime dataTime = LocalDateTime.parse(d.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            return Duration.between(dataTime, LocalDateTime.now()).toMinutes() <= 1440;
                        } catch (Exception e) {
                            System.err.println("⛔️ Parse lỗi với: " + d.getCreatedAt());
                            return false;
                        }
                    })
                    .toList();

            List<AlertHistory> newAlerts = new ArrayList<>();

            for (DeviceData d : dataList) {
                String timeFormatted = d.getCreatedAt().substring(0, 16);
                String roomId = String.valueOf(d.getClassroomId());

                if (d.getTemperature() != null && (d.getTemperature() < 18 || d.getTemperature() > 30)) {
                    newAlerts.add(new AlertHistory(timeFormatted, getDeviceIdByType(roomId, "TEMPERATURE"), "Nhiệt độ", "Vượt ngưỡng"));
                }
                if (d.getHumidity() != null && (d.getHumidity() < 35 || d.getHumidity() > 70)) {
                    newAlerts.add(new AlertHistory(timeFormatted, getDeviceIdByType(roomId, "HUMIDITY"), "Độ ẩm", "Vượt ngưỡng"));
                }
                if (d.getLight() != null && (d.getLight() < 200 || d.getLight() > 1500)) {
                    newAlerts.add(new AlertHistory(timeFormatted, getDeviceIdByType(roomId, "LIGHT"), "Ánh sáng", "Vượt ngưỡng"));
                }
                if (d.getCo2() != null && d.getCo2() > 1500) {
                    newAlerts.add(new AlertHistory(timeFormatted, getDeviceIdByType(roomId, "CO2"), "CO2", "Vượt ngưỡng"));
                }
            }

            generatedAlerts.setAll(newAlerts); // 🔥 Chart sẽ hoạt động ổn định từ đây
            System.out.println("✅ Regenerated alerts: " + newAlerts.size());
        });
    }

    private static String getDeviceIdByType(String classroomId, String type) {
        Long roomId = Long.parseLong(classroomId);
        return getAllDevices().stream()
                .filter(d -> type.equals(d.getType()) && d.getClassroomId() != null && d.getClassroomId().equals(roomId))
                .map(Device::getDeviceId)
                .findFirst()
                .orElse("Không rõ");
    }


    public static ObservableList<AlertHistory> getAlertHistory() {
        return generatedAlerts;
    }

    public static ObservableList<XYChart.Data<String, Number>> getMostAlertsRooms(ObservableList<Classroom> classrooms) {
        // Lấy roomNumber của các classroom đang ACTIVE
        Set<String> activeRoomNumbers = classrooms.stream()
                .filter(c -> "ACTIVE".equals(c.statusProperty().get()))
                .map(Classroom::getRoomNumber)
                .collect(Collectors.toSet());

        // Map: deviceCode → roomNumber
        Map<String, String> deviceCodeToRoom = getAllDevices().stream()
                .filter(d -> d.getDeviceId() != null && d.getRoom() != null)
                .collect(Collectors.toMap(Device::getDeviceId, Device::getRoom));

        // Đếm số lượng alert theo roomNumber
        Map<String, Long> alertCountsByRoom = getAlertHistory().stream()
                .map(alert -> {
                    String deviceCode = alert.deviceIdProperty().get();
                    String room = deviceCodeToRoom.get(deviceCode);
                    return (room != null && activeRoomNumbers.contains(room)) ? room : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(room -> room, Collectors.counting()));

        // Trả về danh sách top 5 room có nhiều alert nhất
        return FXCollections.observableArrayList(
                alertCountsByRoom.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .map(entry -> new XYChart.Data<String, Number>(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }




    public static ObservableList<PieChart.Data> getAlertsByTypeDistribution() {
        Map<String, Long> alertCounts = getAlertHistory().stream()
                .collect(Collectors.groupingBy(alert -> alert.alertTypeProperty().get(), Collectors.counting()));

        alertCounts.forEach((type, count) -> System.out.println("📊 Alert type: " + type + " - " + count));

        return alertCounts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<PieChart.Data> getAlertsByTypeDistributionForRoom(Classroom room) {
        // Lấy tất cả deviceId thuộc phòng học này
        Set<String> deviceIdsInRoom = getAllDevices().stream()
                .filter(device -> room.getRoomNumber().equals(device.getRoom()))  // room.getId() là "class-101"
                .map(Device::getDeviceId)  // ví dụ: DV-101A-01
                .collect(Collectors.toSet());

        // Lọc cảnh báo theo deviceId trong phòng
        Map<String, Long> alertCounts = getAlertHistory().stream()
                .filter(alert -> deviceIdsInRoom.contains(alert.deviceIdProperty().get()))
                .collect(Collectors.groupingBy(
                        alert -> alert.alertTypeProperty().get(),
                        Collectors.counting()
                ));

        // Debug (nếu cần)
        alertCounts.forEach((type, count) ->
                System.out.println("📊 Room " + room.getRoomNumber() + " - Alert type: " + type + " - " + count));

        // Trả về dạng PieChart.Data
        return alertCounts.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }



    public static ObservableList<ClassroomMeasurement> getRoomMeasurements() {
        return allClassrooms.stream()
                .filter(c -> "ACTIVE".equals(c.statusProperty().get()))
                .map(c -> new ClassroomMeasurement(
                        String.format("%.0f%%", c.getHumidity()),
                        String.format("%.1f°C", c.getTemperature()),
                        String.format("%.0f lux", c.getLux()),
                        String.format("%.0f ppm", c.getCo2()),
                        c.getLastUpdated(),
                        getAirQuality(c).toString()
                ))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<PieChart.Data> getRoomQualityByTimeDistribution(Classroom room, Duration duration) {
        Instant now = Instant.now();

        List<DeviceData> filteredData = getAllDeviceData().stream()
                .filter(d -> room.getRoomNumber().equals(d.getClassroomName()))
                .filter(d -> {
                    try {
                        LocalDateTime createdAt = LocalDateTime.parse(d.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        return createdAt.isAfter(LocalDateTime.now().minus(duration));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();

        Map<String, Long> qualityCounts = filteredData.stream()
                .map(d -> {
                    Classroom virtualRoom = new Classroom();
                    virtualRoom.setTemperature(d.getTemperature() != null ? d.getTemperature() : 0);
                    virtualRoom.setHumidity(d.getHumidity() != null ? d.getHumidity() : 0);
                    virtualRoom.setLux(d.getLight() != null ? d.getLight() : 0);
                    virtualRoom.setCo2(d.getCo2() != null ? d.getCo2() : 0);
                    return classifyQuality(getAirQualityScore(virtualRoom)); // Trả về "Tốt", "Trung bình", hoặc "Kém"
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long total = qualityCounts.values().stream().mapToLong(Long::longValue).sum();

        return FXCollections.observableArrayList(
                new PieChart.Data("Tốt", qualityCounts.getOrDefault("Tốt", 0L)),
                new PieChart.Data("Trung bình", qualityCounts.getOrDefault("Trung bình", 0L)),
                new PieChart.Data("Kém", qualityCounts.getOrDefault("Kém", 0L))
        );
    }

    // Hàm phụ để phân loại
    private static String classifyQuality(double score) {
        if (score >= 80) return "Tốt";
        else if (score >= 50) return "Trung bình";
        else return "Kém";
    }


    public static ObservableList<XYChart.Data<Number, String>> getTopQualityTimes(Classroom room) {
        List<DeviceData> allData = getAllDeviceData().stream()
                .filter(d -> String.valueOf(d.getClassroomName()).equals(room.getRoomNumber()))
                .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().length() >= 16)
                .toList();

        Map<String, Double> scoreByTime = allData.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().substring(11, 16), // HH:mm
                        Collectors.averagingDouble(d -> {
                            double temp = d.getTemperature() != null ? d.getTemperature() : 0;
                            double hum = d.getHumidity() != null ? d.getHumidity() : 0;
                            double lux = d.getLight() != null ? d.getLight() : 0;
                            double co2 = d.getCo2() != null ? d.getCo2() : 0;

                            Classroom tempRoom = new Classroom();
                            tempRoom.setTemperature(temp);
                            tempRoom.setHumidity(hum);
                            tempRoom.setLux(lux);
                            tempRoom.setCo2(co2);

                            return getAirQualityScore(tempRoom); // trả về double
                        })
                ));

        return scoreByTime.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new XYChart.Data<>((Number) entry.getValue(), entry.getKey()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<XYChart.Data<String, Number>> getMostAlertsByTime(Classroom room) {
        // Lấy tất cả thiết bị trong phòng đó
        Set<String> deviceIdsInRoom = getAllDevices().stream()
                .filter(device -> room.getRoomNumber().equals(device.getRoom()))
                .map(Device::getDeviceId)
                .collect(Collectors.toSet());

        // Lọc cảnh báo thuộc các thiết bị trong phòng đó
        List<AlertHistory> alerts = getAlertHistory().stream()
                .filter(alert -> alert.getCreatedAt() != null && alert.getCreatedAt().length() >= 16)
                .filter(alert -> deviceIdsInRoom.contains(alert.deviceIdProperty().get()))
                .toList();

        // Đếm số cảnh báo theo thời điểm HH:mm
        Map<String, Long> countByTime = alerts.stream()
                .collect(Collectors.groupingBy(
                        alert -> alert.getCreatedAt().substring(11, 16),
                        Collectors.counting()
                ));

        // Trả về 5 thời điểm có nhiều cảnh báo nhất
        return countByTime.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new XYChart.Data<>(e.getKey(), (Number) e.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }



    public static List<DeviceDataMerged> mergeDeviceData(List<DeviceData> rawData) {
        Map<String, DeviceDataMerged> grouped = new HashMap<>();

        for (DeviceData d : rawData) {
            if (d.getCreatedAt() == null || d.getCreatedAt().length() < 16) continue;
            String timeKey = d.getCreatedAt().substring(0, 16); // yyyy-MM-dd HH:mm

            DeviceDataMerged m = grouped.getOrDefault(timeKey, new DeviceDataMerged());
            m.setCreatedAt(timeKey);

            if (d.getTemperature() != null) m.setTemperature(d.getTemperature());
            if (d.getHumidity() != null) m.setHumidity(d.getHumidity());
            if (d.getCo2() != null) m.setCo2(d.getCo2());
            if (d.getLight() != null) m.setLux(d.getLight());

            grouped.put(timeKey, m);
        }

        return grouped.values().stream()
                .sorted(Comparator.comparing(DeviceDataMerged::getCreatedAt).reversed())
                .limit(3)
                .toList();
    }

    public static Classroom getUpdatedClassroomByIdFromApi(String roomId) {
        return getAllClassroomsFromApi().stream()
                .filter(c -> roomId.equals(c.getId()))
                .findFirst()
                .orElse(null);
    }

}