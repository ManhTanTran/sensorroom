package com.example.smartroom.service;

import com.example.smartroom.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataService {

    private static final ObservableList<Device> allDevices = FXCollections.observableArrayList(
            // Phòng A302 (4)
            new Device("1", "VNPT-T-001", "Cảm biến nhiệt độ", "A302", "25°C", "10-07-2025 10:00:00", "Hoạt động"),
            new Device("2", "VNPT-H-001", "Cảm biến độ ẩm", "A302", "55%", "10-07-2025 10:00:00", "Hoạt động"),
            new Device("3", "VNPT-L-001", "Cảm biến ánh sáng", "A302", "450 lux", "10-07-2025 10:00:00", "Hoạt động"),
            new Device("4", "VNPT-C-001", "Cảm biến CO2", "A302", "800 ppm", "10-07-2025 10:00:00", "Hoạt động"),
            // Phòng A303 (4)
            new Device("5", "VNPT-T-002", "Cảm biến nhiệt độ", "A303", "31°C", "10-07-2025 10:05:00", "Hoạt động"),
            new Device("6", "VNPT-H-002", "Cảm biến độ ẩm", "A303", "75%", "10-07-2025 10:05:00", "Hoạt động"),
            new Device("7", "VNPT-L-003", "Cảm biến ánh sáng", "A303", "1600 lux", "10-07-2025 10:05:00", "Hoạt động"),
            new Device("8", "VNPT-C-002", "Cảm biến CO2", "A303", "1800 ppm", "10-07-2025 10:05:00", "Hoạt động"),
            // Phòng B101 (4)
            new Device("9", "VNPT-T-003", "Cảm biến nhiệt độ", "B101", "28°C", "10-07-2025 10:10:00", "Hoạt động"),
            new Device("10", "VNPT-H-003", "Cảm biến độ ẩm", "B101", "65%", "10-07-2025 10:10:00", "Tạm ngưng"),
            new Device("11", "VNPT-L-004", "Cảm biến ánh sáng", "B101", "550 lux", "10-07-2025 10:10:00", "Hoạt động"),
            new Device("12", "VNPT-C-003", "Cảm biến CO2", "B101", "1200 ppm", "10-07-2025 10:10:00", "Hoạt động"),
            // Phòng B102 (4)
            new Device("13", "VNPT-T-004", "Cảm biến nhiệt độ", "B102", "26°C", "10-07-2025 10:15:00", "Mất kết nối"),
            new Device("14", "VNPT-H-004", "Cảm biến độ ẩm", "B102", "50%", "10-07-2025 10:15:00", "Hoạt động"),
            new Device("15", "VNPT-L-005", "Cảm biến ánh sáng", "B102", "350 lux", "10-07-2025 10:15:00", "Hoạt động"),
            new Device("16", "VNPT-C-004", "Cảm biến CO2", "B102", "900 ppm", "10-07-2025 10:15:00", "Hoạt động"),
            // Phòng C202 (4)
            new Device("17", "VNPT-T-005", "Cảm biến nhiệt độ", "C202", "20°C", "10-07-2025 10:20:00", "Hoạt động"),
            new Device("18", "VNPT-H-005", "Cảm biến độ ẩm", "C202", "40%", "10-07-2025 10:20:00", "Tạm ngưng"),
            new Device("19", "VNPT-L-006", "Cảm biến ánh sáng", "C202", "250 lux", "10-07-2025 10:20:00", "Tạm ngưng"),
            new Device("20", "VNPT-C-005", "Cảm biến CO2", "C202", "1400 ppm", "10-07-2025 10:20:00", "Tạm ngưng")
    );

    private static final ObservableList<Classroom> allClassrooms = FXCollections.observableArrayList(
            // Truyền allDevices vào constructor để Classroom có thể tự tính deviceCount
            new Classroom("A302", "Phòng 302", "Tòa A", "Tầng 3", "Phòng học", "10/06/2025", "Hoạt động", 25.5, 55, 450, 800, allDevices),
            new Classroom("A303", "Phòng 303", "Tòa A", "Tầng 3", "Phòng học", "01/06/2025", "Hoạt động", 31, 75, 1600, 1800, allDevices),
            new Classroom("B101", "Phòng 101", "Tòa B", "Tầng 1", "Phòng Lab", "02/05/2025", "Hoạt động", 28, 65, 550, 1200, allDevices),
            new Classroom("B102", "Phòng 102", "Tòa B", "Tầng 1", "Phòng học", "01/05/2025", "Hoạt động", 26, 50, 350, 900, allDevices),
            new Classroom("C202", "Phòng 202", "Tòa C", "Tầng 2", "Phòng Lab", "01/06/2025", "Tạm ngưng", 20, 40, 250, 1400, allDevices)
    );


    public enum AirQuality { TỐT, TRUNG_BÌNH, KÉM }

    public static AirQuality getAirQuality(Classroom classroom) {
        int totalScore = 0;
        double temp = classroom.getTemperature();
        if (temp >= 22 && temp <= 27) totalScore += 5; else if ((temp >= 18 && temp < 22) || (temp > 27 && temp <= 30)) totalScore += 3;
        double humidity = classroom.getHumidity();
        if (humidity >= 45 && humidity <= 60) totalScore += 5; else if ((humidity >= 35 && humidity < 45) || (humidity > 60 && humidity <= 70)) totalScore += 3;
        double lux = classroom.getLux();
        if (lux >= 300 && lux <= 500) totalScore += 5; else if ((lux >= 200 && lux < 300) || (lux > 500 && lux <= 600)) totalScore += 3; else if (lux > 1500) totalScore += 0; else totalScore += 3;
        int co2 = classroom.getCo2();
        if (co2 <= 1000) totalScore += 5; else if (co2 <= 1500) totalScore += 3;
        double averageScore = totalScore / 4.0;
        if (averageScore >= 4) return AirQuality.TỐT;
        if (averageScore >= 2.5) return AirQuality.TRUNG_BÌNH;
        return AirQuality.KÉM;
    }

    public static ObservableList<PieChart.Data> getRoomQualityDistribution() {
        Map<AirQuality, Long> qualityCounts = getAllClassrooms().stream().collect(Collectors.groupingBy(DataService::getAirQuality, Collectors.counting()));
        return qualityCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey().toString().replace("_", " "), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private static final ObservableList<AlertHistory> alertHistory = FXCollections.observableArrayList(
            new AlertHistory("12:05:30 08/06/2025", "VNPT-T-001", "Nhiệt độ", "Vượt ngưỡng"),
            new AlertHistory("11:55:10 08/06/2025", "VNPT-H-002", "Độ ẩm", "Bình thường"),
            new AlertHistory("10:35:05 07/08/2025", "VNPT-L-002", "Ánh sáng", "Vượt ngưỡng"),
            new AlertHistory("10:26:05 07/08/2025", "VNPT-H-001", "Độ ẩm", "Mất kết nối")
    );

    public static ObservableList<Device> getAllDevices() { return allDevices; }
    public static ObservableList<Classroom> getAllClassrooms() { return allClassrooms; }
    public static void addClassroom(Classroom classroom) { allClassrooms.add(classroom); }
    public static List<Device> getDevicesByRoomId(String roomId) { return getAllDevices().stream().filter(device -> roomId.equals(device.getRoom())).collect(Collectors.toList()); }
    public static ObservableList<Device> getDevicesForKtv(List<String> managedRooms) { return allDevices.stream().filter(device -> managedRooms.contains(device.getRoom())).collect(Collectors.toCollection(FXCollections::observableArrayList)); }
    public static ObservableList<Classroom> getClassroomsForKtv(List<String> managedRooms) { return allClassrooms.stream().filter(classroom -> managedRooms.contains(classroom.getId())).collect(Collectors.toCollection(FXCollections::observableArrayList)); }
    public static ObservableList<PieChart.Data> getSensorTypeData(ObservableList<Device> devices) { Map<String, Long> typeCounts = devices.stream().collect(Collectors.groupingBy(Device::getType, Collectors.counting())); return typeCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey(), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList)); }
    public static ObservableList<AlertHistory> getAlertHistory() { return alertHistory; }
    public static ObservableList<XYChart.Data<Number, String>> getTopQualityRooms() { return FXCollections.observableArrayList(new XYChart.Data<>(95, "A303"), new XYChart.Data<>(92, "B101"), new XYChart.Data<>(88, "A302")); }
    public static ObservableList<XYChart.Data<String, Number>> getMostAlertsRooms() { return FXCollections.observableArrayList(new XYChart.Data<>("A302", 8), new XYChart.Data<>("B101", 6), new XYChart.Data<>("C202", 5)); }
    public static ObservableList<PieChart.Data> getAlertsByTypeDistribution() { Map<String, Long> alertCounts = getAlertHistory().stream().filter(alert -> !"Bình thường".equals(alert.warningProperty().get())).collect(Collectors.groupingBy(alert -> alert.alertTypeProperty().get(), Collectors.counting())); return alertCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey(), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList)); }

    /**
     * PHƯƠNG THỨC BỊ THIẾU ĐÃ ĐƯỢC THÊM LẠI
     */
    public static ObservableList<ClassroomMeasurement> getRoomMeasurements() {
        // Trả về dữ liệu tĩnh cho bảng này
        return FXCollections.observableArrayList(
                new ClassroomMeasurement("80%", "32°C", "800 lux", "1000 ppm", "12:20:30", "Tốt"),
                new ClassroomMeasurement("50%", "33°C", "700 lux", "1200 ppm", "12:15:30", "Kém"),
                new ClassroomMeasurement("70%", "30°C", "600 lux", "1250 ppm", "12:10:30", "Trung bình")
        );
    }
}