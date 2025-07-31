package com.example.smartroom.service;

import com.example.smartroom.model.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataService {

    // TẠO LIST VỚI EXTRACTOR ĐỂ THEO DÕI THAY ĐỔI THUỘC TÍNH
    private static final ObservableList<Device> allDevices = FXCollections.observableArrayList(
            device -> new Observable[] {device.roomProperty(), device.statusProperty()}
    );

    private static final ObservableList<Classroom> allClassrooms = FXCollections.observableArrayList();
    static {
        // Dữ liệu mẫu đã được tạo thủ công cho 20 phòng và 88 thiết bị

        // --- Phòng A101 ---
        allDevices.add(new Device("1", "VNPT-T-A101", "Cảm biến nhiệt độ", "A101", "26°C", "12-07-2025 08:00:00", "Hoạt động"));
        allDevices.add(new Device("2", "VNPT-H-A101", "Cảm biến độ ẩm", "A101", "58%", "12-07-2025 08:00:00", "Hoạt động"));
        allDevices.add(new Device("3", "VNPT-L-A101", "Cảm biến ánh sáng", "A101", "450 lux", "12-07-2025 08:00:00", "Hoạt động"));
        allDevices.add(new Device("4", "VNPT-C-A101", "Cảm biến CO2", "A101", "900 ppm", "12-07-2025 08:00:00", "Hoạt động"));
        allClassrooms.add(new Classroom("A101", "Phòng 101", "Tòa A", "Tầng 1", "Phòng học", "12/07/2025", "Hoạt động", 26, 58, 450, 900, allDevices));

        // --- Phòng A102 ---
        allDevices.add(new Device("5", "VNPT-T-A102", "Cảm biến nhiệt độ", "A102", "31°C", "12-07-2025 08:05:00", "Hoạt động"));
        allDevices.add(new Device("6", "VNPT-H-A102", "Cảm biến độ ẩm", "A102", "72%", "12-07-2025 08:05:00", "Hoạt động"));
        allDevices.add(new Device("7", "VNPT-L-A102", "Cảm biến ánh sáng", "A102", "1600 lux", "12-07-2025 08:05:00", "Hoạt động"));
        allDevices.add(new Device("8", "VNPT-C-A102", "Cảm biến CO2", "A102", "1750 ppm", "12-07-2025 08:05:00", "Hoạt động"));
        allClassrooms.add(new Classroom("A102", "Phòng 102", "Tòa A", "Tầng 1", "Phòng Lab", "12/07/2025", "Hoạt động", 31, 72, 1600, 1750, allDevices));

        // --- Phòng A201 ---
        allDevices.add(new Device("9", "VNPT-T-A201", "Cảm biến nhiệt độ", "A201", "24°C", "12-07-2025 08:10:00", "Tạm ngưng"));
        allDevices.add(new Device("10", "VNPT-H-A201", "Cảm biến độ ẩm", "A201", "65%", "12-07-2025 08:10:00", "Hoạt động"));
        allDevices.add(new Device("11", "VNPT-L-A201", "Cảm biến ánh sáng", "A201", "500 lux", "12-07-2025 08:10:00", "Hoạt động"));
        allDevices.add(new Device("12", "VNPT-C-A201", "Cảm biến CO2", "A201", "1100 ppm", "12-07-2025 08:10:00", "Hoạt động"));
        allClassrooms.add(new Classroom("A201", "Phòng 201", "Tòa A", "Tầng 2", "Phòng học", "12/07/2025", "Tạm ngưng", 24, 65, 500, 1100, allDevices));

        // --- Phòng A202 ---
        allDevices.add(new Device("13", "VNPT-T-A202", "Cảm biến nhiệt độ", "A202", "29°C", "12-07-2025 08:15:00", "Hoạt động"));
        allDevices.add(new Device("14", "VNPT-H-A202", "Cảm biến độ ẩm", "A202", "68%", "12-07-2025 08:15:00", "Hoạt động"));
        allDevices.add(new Device("15", "VNPT-L-A202", "Cảm biến ánh sáng", "A202", "300 lux", "12-07-2025 08:15:00", "Mất kết nối"));
        allDevices.add(new Device("16", "VNPT-C-A202", "Cảm biến CO2", "A202", "950 ppm", "12-07-2025 08:15:00", "Hoạt động"));
        allClassrooms.add(new Classroom("A202", "Phòng 202", "Tòa A", "Tầng 2", "Phòng học", "12/07/2025", "Hoạt động", 29, 68, 300, 950, allDevices));

        // --- Phòng B101 ---
        allDevices.add(new Device("17", "VNPT-T-B101", "Cảm biến nhiệt độ", "B101", "22°C", "12-07-2025 08:20:00", "Hoạt động"));
        allDevices.add(new Device("18", "VNPT-H-B101", "Cảm biến độ ẩm", "B101", "55%", "12-07-2025 08:20:00", "Hoạt động"));
        allDevices.add(new Device("19", "VNPT-L-B101", "Cảm biến ánh sáng", "B101", "480 lux", "12-07-2025 08:20:00", "Hoạt động"));
        allDevices.add(new Device("20", "VNPT-C-B101", "Cảm biến CO2", "B101", "850 ppm", "12-07-2025 08:20:00", "Hoạt động"));
        allClassrooms.add(new Classroom("B101", "Phòng 101", "Tòa B", "Tầng 1", "Phòng Lab", "12/07/2025", "Hoạt động", 22, 55, 480, 850, allDevices));

        // --- Thêm 15 phòng và 60 thiết bị nữa ---
        // Phòng 6
        allDevices.add(new Device("21", "VNPT-T-B102", "Cảm biến nhiệt độ", "B102", "17°C", "12-07-2025 08:25:00", "Hoạt động"));
        allDevices.add(new Device("22", "VNPT-H-B102", "Cảm biến độ ẩm", "B102", "50%", "12-07-2025 08:25:00", "Hoạt động"));
        allDevices.add(new Device("23", "VNPT-L-B102", "Cảm biến ánh sáng", "B102", "400 lux", "12-07-2025 08:25:00", "Hoạt động"));
        allDevices.add(new Device("24", "VNPT-C-B102", "Cảm biến CO2", "B102", "1300 ppm", "12-07-2025 08:25:00", "Hoạt động"));
        allClassrooms.add(new Classroom("B102", "Phòng 102", "Tòa B", "Tầng 1", "Phòng học", "12/07/2025", "Hoạt động", 17, 50, 400, 1300, allDevices));

        // Phòng 7
        allDevices.add(new Device("25", "VNPT-T-B201", "Cảm biến nhiệt độ", "B201", "28°C", "12-07-2025 08:30:00", "Hoạt động"));
        allDevices.add(new Device("26", "VNPT-H-B201", "Cảm biến độ ẩm", "B201", "75%", "12-07-2025 08:30:00", "Hoạt động"));
        allDevices.add(new Device("27", "VNPT-L-B201", "Cảm biến ánh sáng", "B201", "600 lux", "12-07-2025 08:30:00", "Hoạt động"));
        allDevices.add(new Device("28", "VNPT-C-B201", "Cảm biến CO2", "B201", "1000 ppm", "12-07-2025 08:30:00", "Hoạt động"));
        allClassrooms.add(new Classroom("B201", "Phòng 201", "Tòa B", "Tầng 2", "Phòng học", "12/07/2025", "Hoạt động", 28, 75, 600, 1000, allDevices));

        // Phòng 8
        allDevices.add(new Device("29", "VNPT-T-B202", "Cảm biến nhiệt độ", "B202", "26°C", "12-07-2025 08:35:00", "Hoạt động"));
        allDevices.add(new Device("30", "VNPT-H-B202", "Cảm biến độ ẩm", "B202", "45%", "12-07-2025 08:35:00", "Hoạt động"));
        allDevices.add(new Device("31", "VNPT-L-B202", "Cảm biến ánh sáng", "B202", "190 lux", "12-07-2025 08:35:00", "Hoạt động"));
        allDevices.add(new Device("32", "VNPT-C-B202", "Cảm biến CO2", "B202", "1600 ppm", "12-07-2025 08:35:00", "Hoạt động"));
        allClassrooms.add(new Classroom("B202", "Phòng 202", "Tòa B", "Tầng 2", "Phòng Lab", "12/07/2025", "Hoạt động", 26, 45, 190, 1600, allDevices));

        // Phòng 9
        allDevices.add(new Device("33", "VNPT-T-C101", "Cảm biến nhiệt độ", "C101", "25°C", "12-07-2025 08:40:00", "Hoạt động"));
        allDevices.add(new Device("34", "VNPT-H-C101", "Cảm biến độ ẩm", "C101", "59%", "12-07-2025 08:40:00", "Tạm ngưng"));
        allDevices.add(new Device("35", "VNPT-L-C101", "Cảm biến ánh sáng", "C101", "520 lux", "12-07-2025 08:40:00", "Hoạt động"));
        allDevices.add(new Device("36", "VNPT-C-C101", "Cảm biến CO2", "C101", "920 ppm", "12-07-2025 08:40:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C101", "Phòng 101", "Tòa C", "Tầng 1", "Phòng học", "12/07/2025", "Hoạt động", 25, 59, 520, 920, allDevices));

        // Phòng 10
        allDevices.add(new Device("37", "VNPT-T-C102", "Cảm biến nhiệt độ", "C102", "27°C", "12-07-2025 08:45:00", "Hoạt động"));
        allDevices.add(new Device("38", "VNPT-H-C102", "Cảm biến độ ẩm", "C102", "61%", "12-07-2025 08:45:00", "Hoạt động"));
        allDevices.add(new Device("39", "VNPT-L-C102", "Cảm biến ánh sáng", "C102", "470 lux", "12-07-2025 08:45:00", "Hoạt động"));
        allDevices.add(new Device("40", "VNPT-C-C102", "Cảm biến CO2", "C102", "1150 ppm", "12-07-2025 08:45:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C102", "Phòng 102", "Tòa C", "Tầng 1", "Phòng học", "12/07/2025", "Hoạt động", 27, 61, 470, 1150, allDevices));

        // Phòng 11
        allDevices.add(new Device("41", "VNPT-T-C201", "Cảm biến nhiệt độ", "C201", "32°C", "12-07-2025 08:50:00", "Hoạt động"));
        allDevices.add(new Device("42", "VNPT-H-C201", "Cảm biến độ ẩm", "C201", "44%", "12-07-2025 08:50:00", "Hoạt động"));
        allDevices.add(new Device("43", "VNPT-L-C201", "Cảm biến ánh sáng", "C201", "580 lux", "12-07-2025 08:50:00", "Hoạt động"));
        allDevices.add(new Device("44", "VNPT-C-C201", "Cảm biến CO2", "C201", "1450 ppm", "12-07-2025 08:50:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C201", "Phòng 201", "Tòa C", "Tầng 2", "Phòng Lab", "12/07/2025", "Hoạt động", 32, 44, 580, 1450, allDevices));

        // Phòng 12
        allDevices.add(new Device("45", "VNPT-T-C202", "Cảm biến nhiệt độ", "C202", "26°C", "12-07-2025 08:55:00", "Hoạt động"));
        allDevices.add(new Device("46", "VNPT-H-C202", "Cảm biến độ ẩm", "C202", "55%", "12-07-2025 08:55:00", "Hoạt động"));
        allDevices.add(new Device("47", "VNPT-L-C202", "Cảm biến ánh sáng", "C202", "500 lux", "12-07-2025 08:55:00", "Hoạt động"));
        allDevices.add(new Device("48", "VNPT-C-C202", "Cảm biến CO2", "C202", "880 ppm", "12-07-2025 08:55:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C202", "Phòng 202", "Tòa C", "Tầng 2", "Phòng học", "12/07/2025", "Hoạt động", 26, 55, 500, 880, allDevices));

        // Phòng 13
        allDevices.add(new Device("49", "VNPT-T-C301", "Cảm biến nhiệt độ", "C301", "24°C", "12-07-2025 09:00:00", "Tạm ngưng"));
        allDevices.add(new Device("50", "VNPT-H-C301", "Cảm biến độ ẩm", "C301", "62%", "12-07-2025 09:00:00", "Tạm ngưng"));
        allDevices.add(new Device("51", "VNPT-L-C301", "Cảm biến ánh sáng", "C301", "450 lux", "12-07-2025 09:00:00", "Hoạt động"));
        allDevices.add(new Device("52", "VNPT-C-C301", "Cảm biến CO2", "C301", "1050 ppm", "12-07-2025 09:00:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C301", "Phòng 301", "Tòa C", "Tầng 3", "Phòng học", "12/07/2025", "Tạm ngưng", 24, 62, 450, 1050, allDevices));

        // Phòng 14
        allDevices.add(new Device("53", "VNPT-T-C302", "Cảm biến nhiệt độ", "C302", "27°C", "12-07-2025 09:05:00", "Hoạt động"));
        allDevices.add(new Device("54", "VNPT-H-C302", "Cảm biến độ ẩm", "C302", "58%", "12-07-2025 09:05:00", "Hoạt động"));
        allDevices.add(new Device("55", "VNPT-L-C302", "Cảm biến ánh sáng", "C302", "530 lux", "12-07-2025 09:05:00", "Hoạt động"));
        allDevices.add(new Device("56", "VNPT-C-C302", "Cảm biến CO2", "C302", "930 ppm", "12-07-2025 09:05:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C302", "Phòng 302", "Tòa C", "Tầng 3", "Phòng Lab", "12/07/2025", "Hoạt động", 27, 58, 530, 930, allDevices));

        // Phòng 15
        allDevices.add(new Device("57", "VNPT-T-C401", "Cảm biến nhiệt độ", "C401", "25°C", "12-07-2025 09:10:00", "Hoạt động"));
        allDevices.add(new Device("58", "VNPT-H-C401", "Cảm biến độ ẩm", "C401", "56%", "12-07-2025 09:10:00", "Hoạt động"));
        allDevices.add(new Device("59", "VNPT-L-C401", "Cảm biến ánh sáng", "C401", "490 lux", "12-07-2025 09:10:00", "Hoạt động"));
        allDevices.add(new Device("60", "VNPT-C-C401", "Cảm biến CO2", "C401", "870 ppm", "12-07-2025 09:10:00", "Mất kết nối"));
        allClassrooms.add(new Classroom("C401", "Phòng 401", "Tòa C", "Tầng 4", "Phòng học", "12/07/2025", "Hoạt động", 25, 56, 490, 870, allDevices));

        // Phòng 16
        allDevices.add(new Device("61", "VNPT-T-C402", "Cảm biến nhiệt độ", "C402", "26°C", "12-07-2025 09:15:00", "Hoạt động"));
        allDevices.add(new Device("62", "VNPT-H-C402", "Cảm biến độ ẩm", "C402", "60%", "12-07-2025 09:15:00", "Hoạt động"));
        allDevices.add(new Device("63", "VNPT-L-C402", "Cảm biến ánh sáng", "C402", "460 lux", "12-07-2025 09:15:00", "Hoạt động"));
        allDevices.add(new Device("64", "VNPT-C-C402", "Cảm biến CO2", "C402", "910 ppm", "12-07-2025 09:15:00", "Hoạt động"));
        allClassrooms.add(new Classroom("C402", "Phòng 402", "Tòa C", "Tầng 4", "Phòng học", "12/07/2025", "Hoạt động", 26, 60, 460, 910, allDevices));

        // Phòng 17
        allDevices.add(new Device("65", "VNPT-T-A301", "Cảm biến nhiệt độ", "A301", "29°C", "12-07-2025 09:20:00", "Hoạt động"));
        allDevices.add(new Device("66", "VNPT-H-A301", "Cảm biến độ ẩm", "A301", "68%", "12-07-2025 09:20:00", "Hoạt động"));
        allDevices.add(new Device("67", "VNPT-L-A301", "Cảm biến ánh sáng", "A301", "540 lux", "12-07-2025 09:20:00", "Hoạt động"));
        allDevices.add(new Device("68", "VNPT-C-A301", "Cảm biến CO2", "A301", "1250 ppm", "12-07-2025 09:20:00", "Hoạt động"));
        allClassrooms.add(new Classroom("A301", "Phòng 301", "Tòa A", "Tầng 3", "Phòng Lab", "12/07/2025", "Hoạt động", 29, 68, 540, 1250, allDevices));

        // Phòng 18
        allDevices.add(new Device("69", "VNPT-T-A302", "Cảm biến nhiệt độ", "A302", "25°C", "12-07-2025 09:25:00", "Hoạt động"));
        allDevices.add(new Device("70", "VNPT-H-A302", "Cảm biến độ ẩm", "A302", "55%", "12-07-2025 09:25:00", "Hoạt động"));
        allDevices.add(new Device("71", "VNPT-L-A302", "Cảm biến ánh sáng", "A302", "450 lux", "12-07-2025 09:25:00", "Hoạt động"));
        allDevices.add(new Device("72", "VNPT-C-A302", "Cảm biến CO2", "A302", "800 ppm", "12-07-2025 09:25:00", "Hoạt động"));
        allClassrooms.add(new Classroom("A302", "Phòng 302", "Tòa A", "Tầng 3", "Phòng học", "12/07/2025", "Hoạt động", 25, 55, 450, 800, allDevices));

        // Phòng 19
        allDevices.add(new Device("73", "VNPT-T-B301", "Cảm biến nhiệt độ", "B301", "28°C", "12-07-2025 09:30:00", "Tạm ngưng"));
        allDevices.add(new Device("74", "VNPT-H-B301", "Cảm biến độ ẩm", "B301", "63%", "12-07-2025 09:30:00", "Hoạt động"));
        allDevices.add(new Device("75", "VNPT-L-B301", "Cảm biến ánh sáng", "B301", "510 lux", "12-07-2025 09:30:00", "Tạm ngưng"));
        allDevices.add(new Device("76", "VNPT-C-B301", "Cảm biến CO2", "B301", "980 ppm", "12-07-2025 09:30:00", "Hoạt động"));
        allClassrooms.add(new Classroom("B301", "Phòng 301", "Tòa B", "Tầng 3", "Phòng học", "12/07/2025", "Tạm ngưng", 28, 63, 510, 980, allDevices));

        // Phòng 20
        allDevices.add(new Device("77", "VNPT-T-B302", "Cảm biến nhiệt độ", "B302", "26°C", "12-07-2025 09:35:00", "Hoạt động"));
        allDevices.add(new Device("78", "VNPT-H-B302", "Cảm biến độ ẩm", "B302", "57%", "12-07-2025 09:35:00", "Hoạt động"));
        allDevices.add(new Device("79", "VNPT-L-B302", "Cảm biến ánh sáng", "B302", "480 lux", "12-07-2025 09:35:00", "Hoạt động"));
        allDevices.add(new Device("80", "VNPT-C-B302", "Cảm biến CO2", "B302", "820 ppm", "12-07-2025 09:35:00", "Hoạt động"));
        allClassrooms.add(new Classroom("B302", "Phòng 302", "Tòa B", "Tầng 3", "Phòng Lab", "12/07/2025", "Hoạt động", 26, 57, 480, 820, allDevices));

        // --- 8 THIẾT BỊ CÓ TRẠNG THÁI "SẴN SÀNG" ---
        allDevices.addAll(List.of(
                new Device("81", "VNPT-T-100", "Cảm biến nhiệt độ", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("82", "VNPT-T-101", "Cảm biến nhiệt độ", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("83", "VNPT-H-100", "Cảm biến độ ẩm", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("84", "VNPT-H-101", "Cảm biến độ ẩm", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("85", "VNPT-L-100", "Cảm biến ánh sáng", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("86", "VNPT-L-101", "Cảm biến ánh sáng", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("87", "VNPT-C-100", "Cảm biến CO2", null, "Chưa đo", "N/A", "Sẵn sàng"),
                new Device("88", "VNPT-C-101", "Cảm biến CO2", null, "Chưa đo", "N/A", "Sẵn sàng")
        ));
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
        double temp = classroom.getTemperature();
        if (temp >= 22 && temp <= 27) totalScore += 5; else if ((temp >= 18 && temp < 22) || (temp > 27 && temp <= 30)) totalScore += 3;
        double humidity = classroom.getHumidity();
        if (humidity >= 45 && humidity <= 60) totalScore += 5; else if ((humidity >= 35 && humidity < 45) || (humidity > 60 && humidity <= 70)) totalScore += 3;
        double lux = classroom.getLux();
        if (lux >= 300 && lux <= 500) totalScore += 5; else if ((lux >= 200 && lux < 300) || (lux > 500 && lux <= 600)) totalScore += 3; else if (lux > 1500) totalScore += 0; else totalScore += 3;
        int co2 = classroom.getCo2();
        if (co2 <= 1000) totalScore += 5; else if (co2 <= 1500) totalScore += 3;
        return totalScore / 4.0;
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

        int co2 = classroom.getCo2();
        // CO2 ở mức có hại (> 1500)
        if (co2 > 1500) return true;

        // Nếu tất cả các chỉ số đều trong ngưỡng chấp nhận được
        return false;
    }
    public static ObservableList<PieChart.Data> getRoomQualityDistribution(ObservableList<Classroom> classrooms) {
        Map<AirQuality, Long> qualityCounts = classrooms.stream().collect(Collectors.groupingBy(DataService::getAirQuality, Collectors.counting()));
        return qualityCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey().toString().replace("_", " "), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private static final ObservableList<AlertHistory> alertHistory = FXCollections.observableArrayList(
            new AlertHistory("12:05:30 08/06/2025", "VNPT-T-001", "Nhiệt độ", "Vượt ngưỡng"),
            new AlertHistory("11:55:10 08/06/2025", "VNPT-H-002", "Độ ẩm", "Bình thường"),
            new AlertHistory("10:35:05 07/08/2025", "VNPT-L-002", "Ánh sáng", "Vượt ngưỡng"),
            new AlertHistory("10:26:05 07/08/2025", "VNPT-H-001", "Độ ẩm", "Mất kết nối"),
            new AlertHistory("09:15:45 07/08/2025", "VNPT-T-002", "Nhiệt độ", "Vượt ngưỡng"),
            new AlertHistory("09:12:00 07/08/2025", "VNPT-C-001", "CO2", "Vượt ngưỡng"), // THÊM CẢNH BÁO CO2
            new AlertHistory("09:05:12 07/08/2025", "VNPT-L-001", "Ánh sáng", "Bình thường")
    );

    public static ObservableList<Device> getAllDevices() { return allDevices; }
    public static ObservableList<Classroom> getAllClassrooms() { return allClassrooms; }
    public static void addClassroom(Classroom classroom) { allClassrooms.add(classroom); }
    public static List<Device> getDevicesByRoomId(String roomId) { return getAllDevices().stream().filter(device -> roomId.equals(device.getRoom())).collect(Collectors.toList()); }
    public static ObservableList<Device> getDevicesForKtv(List<String> managedRooms) {
        return allDevices.stream()
                .filter(device -> device.getRoom() != null && managedRooms.contains(device.getRoom()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
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
    public static ObservableList<AlertHistory> getAlertHistory() { return alertHistory; }
    public static ObservableList<XYChart.Data<Number, String>> getTopQualityRooms(ObservableList<Classroom> classrooms) {
        return classrooms.stream()
                .filter(c -> "Hoạt động".equals(c.statusProperty().get()))
                .sorted(Comparator.comparingDouble(DataService::getAirQualityScore).reversed())
                .limit(5)
                .map(c -> new XYChart.Data<>((Number)(getAirQualityScore(c) * 20), c.getId()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static ObservableList<XYChart.Data<String, Number>> getMostAlertsRooms(ObservableList<Classroom> classrooms) {
        Map<String, Long> alertCountsByRoom = getAlertHistory().stream()
                .filter(alert -> !"Bình thường".equals(alert.warningProperty().get()))
                .collect(Collectors.groupingBy(
                        alert -> getAllDevices().stream()
                                .filter(device -> device.imeiProperty().get().equals(alert.deviceNameProperty().get()))
                                .map(Device::getRoom)
                                .findFirst()
                                .orElse("Không xác định"),
                        Collectors.counting()
                ));

        return classrooms.stream()
                .filter(c -> "Hoạt động".equals(c.statusProperty().get()))
                .map(c -> new XYChart.Data<>(c.getId(), (Number)alertCountsByRoom.getOrDefault(c.getId(), 0L)))
                .sorted(Comparator.comparingDouble(d -> ((XYChart.Data<String, Number>) d).getYValue().doubleValue()).reversed())
                .limit(5)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    public static ObservableList<PieChart.Data> getAlertsByTypeDistribution() { Map<String, Long> alertCounts = getAlertHistory().stream().filter(alert -> !"Bình thường".equals(alert.warningProperty().get())).collect(Collectors.groupingBy(alert -> alert.alertTypeProperty().get(), Collectors.counting())); return alertCounts.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey(), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList)); }
    public static ObservableList<XYChart.Data<String, Number>> getAlertsByTypeData() { return FXCollections.observableArrayList(new XYChart.Data<>("Nhiệt độ", 5), new XYChart.Data<>("Độ ẩm", 3), new XYChart.Data<>("Ánh sáng", 4), new XYChart.Data<>("CO2", 2)); }
    public static ObservableList<ClassroomMeasurement> getRoomMeasurements() {
        // Trả về dữ liệu tĩnh cho bảng này
        return FXCollections.observableArrayList(
                new ClassroomMeasurement("80%", "32°C", "800 lux", "1000 ppm", "12:20:30", "Tốt"),
                new ClassroomMeasurement("50%", "33°C", "700 lux", "1200 ppm", "12:15:30", "Kém"),
                new ClassroomMeasurement("70%", "30°C", "600 lux", "1250 ppm", "12:10:30", "Trung bình")
        );
    }

    public static ObservableList<XYChart.Data<Number, String>> getTopQualityTimes() {
        return FXCollections.observableArrayList(
                new XYChart.Data<>(98, "10:00 AM"),
                new XYChart.Data<>(95, "02:00 PM"),
                new XYChart.Data<>(92, "09:00 AM"),
                new XYChart.Data<>(88, "04:00 PM"),
                new XYChart.Data<>(85, "11:00 AM")
        );
    }

    /**
     * DỮ LIỆU MỚI: Top 5 mốc thời gian có cảnh báo nhiều nhất
     */
    public static ObservableList<XYChart.Data<String, Number>> getMostAlertsByTime() {
        return FXCollections.observableArrayList(
                new XYChart.Data<>("12:00 AM", 5),
                new XYChart.Data<>("10:00 AM", 4),
                new XYChart.Data<>("02:00 PM", 3),
                new XYChart.Data<>("04:00 PM", 2),
                new XYChart.Data<>("09:00 AM", 1)
        );
    }
}