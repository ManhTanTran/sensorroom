package com.example.smartroom.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Lớp này khớp với DeviceDataResponse
public class DeviceData {
    private long id;
    private Double temperature;
    private Double humidity;
    private Double light;
    private Double co2;
    private String createdAt;
    @SerializedName("deviceCode")
    private String deviceId;
    private String deviceName;
    private long classroomId;
    private String classroomName;

    public DeviceData() {}

    // Getters
    public Double getTemperature() { return temperature; }
    public Double getHumidity() { return humidity; }
    public Double getLight() { return light; }
    public Double getCo2() { return co2; }
    public String getDeviceId() { return deviceId; }
    public String getRoomId() {
        return String.valueOf(classroomId);
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public long getClassroomId() {
        return classroomId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getFormattedCreatedAt() {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dt = LocalDateTime.parse(createdAt, inputFormatter);
            return dt.format(outputFormatter);
        } catch (Exception e) {
            return createdAt; // fallback nếu parse lỗi
        }
    }



}