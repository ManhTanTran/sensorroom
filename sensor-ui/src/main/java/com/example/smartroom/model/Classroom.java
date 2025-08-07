package com.example.smartroom.model;

import com.example.smartroom.service.DataService;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;

import java.util.List;

public class Classroom {
    private long id;
    private String code;
    private String name;
    private String building;
    private String floor;
    private String status;
    private String createdAt;
    private int deviceCount;

    @SerializedName("roomtype")
    private String roomType;

    private String displayRoomType;

    private transient StringProperty fxId = new SimpleStringProperty();
    private transient StringProperty fxRoomNumber = new SimpleStringProperty();
    private transient StringProperty fxBuilding = new SimpleStringProperty();
    private transient StringProperty fxFloor = new SimpleStringProperty();
    private transient IntegerProperty fxDeviceCount = new SimpleIntegerProperty();
    private transient StringProperty fxCreatedAt = new SimpleStringProperty();
    private transient StringProperty fxStatus = new SimpleStringProperty();
    private transient StringProperty fxRoomType = new SimpleStringProperty();
    private transient StringProperty fxDisplayRoomType = new SimpleStringProperty(); // THÊM MỚI
    private transient StringProperty lastUpdated = new SimpleStringProperty();

    private transient DoubleProperty temperature = new SimpleDoubleProperty(0);
    private transient DoubleProperty humidity = new SimpleDoubleProperty(0);
    private transient DoubleProperty lux = new SimpleDoubleProperty(0);
    private transient DoubleProperty co2 = new SimpleDoubleProperty(0);

    public Classroom() {}

    public void postProcess() {
        fxId.set(code);
        fxRoomNumber.set(name);
        fxBuilding.set(building);
        fxFloor.set(floor);
        fxDeviceCount.set(deviceCount);
        fxCreatedAt.set(createdAt);
        fxStatus.set(status);
        fxRoomType.set(roomType);

        if ("LAB".equalsIgnoreCase(roomType)) {
            displayRoomType = "Phòng Lab";
        } else if ("THUONG".equalsIgnoreCase(roomType)) {
            displayRoomType = "Phòng Thường";
        } else {
            displayRoomType = "Không xác định";
        }
        fxDisplayRoomType.set(displayRoomType);

        List<DeviceData> dataInRoom = DataService.getAllDeviceData().stream()
                .filter(d -> d.getClassroomId() == this.id)
                .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().length() >= 16)
                .toList();

        List<DeviceDataMerged> merged = DataService.mergeDeviceData(dataInRoom);

        if (!merged.isEmpty()) {
            DeviceDataMerged latest = merged.get(0); // Dòng mới nhất đã merge

            setTemperature(latest.getTemperature() != null ? latest.getTemperature() : 0);
            setHumidity(latest.getHumidity() != null ? latest.getHumidity() : 0);
            setLux(latest.getLux() != null ? latest.getLux() : 0);
            setCo2(latest.getCo2() != null ? latest.getCo2() : 0);
            setLastUpdated(latest.getCreatedAt());

            System.out.println("↪ Merge OK cho " + name + ": T=" + getTemperature() + " H=" + getHumidity() + " C=" + getCo2() + " L=" + getLux());
        }
    }

    // Property cho TableView
    public StringProperty idProperty() { return fxId; }
    public StringProperty roomNumberProperty() { return fxRoomNumber; }
    public StringProperty buildingProperty() { return fxBuilding; }
    public StringProperty floorProperty() { return fxFloor; }
    public IntegerProperty deviceCountProperty() { return fxDeviceCount; }
    public StringProperty creationDateProperty() { return fxCreatedAt; }
    public StringProperty statusProperty() { return fxStatus; }
    public StringProperty roomTypeProperty() { return fxRoomType; }
    public StringProperty displayRoomTypeProperty() { return fxDisplayRoomType; }
    private transient List<Device> devicesInRoom = new java.util.ArrayList<>();

    // Getter & Setter
    public List<Device> getDevicesInRoom() {
        return devicesInRoom != null ? devicesInRoom : List.of();
    }


    public void setDevicesInRoom(List<Device> devices) {
        this.devicesInRoom = devices;
    }// MỚI

    public void setCreationDate(String date) {
        this.createdAt = date;
        this.fxCreatedAt.set(date);
    }

    // Getter thông thường
    public String getId() { return code; }
    public String getRoomNumber() { return name; }
    public String getStatus() { return status; }
    public String getRoomType() { return roomType; }
    public String getDisplayRoomType() { return displayRoomType; } // MỚI

    // Sensor readings
    public double getTemperature() { return temperature.get(); }
    public double getHumidity() { return humidity.get(); }
    public double getLux() { return lux.get(); }
    public double getCo2() { return co2.get(); }

    public void setTemperature(double temp) { this.temperature.set(temp); }
    public void setHumidity(double hum) { this.humidity.set(hum); }
    public void setLux(double lux) { this.lux.set(lux); }
    public void setCo2(double co2) { this.co2.set(co2); }

    public void setStatus(String newStatus) {
        this.status = newStatus;
        this.fxStatus.set(newStatus);
    }

    public String getFormattedStatus() {
        return switch (status) {
            case "ACTIVE" -> "Hoạt động";
            case "INACTIVE", "MAINTENANCE" -> "Tạm ngưng";
            default -> status;
        };
    }

    public String getLastUpdated() {
        return lastUpdated.get();
    }

    public void setLastUpdated(String value) {
        this.lastUpdated.set(value);
    }

    public StringProperty lastUpdatedProperty() {
        return lastUpdated;
    }
}
