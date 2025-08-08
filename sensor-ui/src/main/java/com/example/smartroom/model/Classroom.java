package com.example.smartroom.model;

import com.example.smartroom.service.DataService;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Classroom {
    private long id;
    private String code;
    private String name;
    private String building;
    private String floor;
    private String status;
    private String createdAt;  // Raw string từ server hoặc set bằng tay
    private int deviceCount;

    @SerializedName("roomtype")
    private String roomType;

    private String displayRoomType;

    private final transient StringProperty fxId = new SimpleStringProperty();
    private final transient StringProperty fxRoomNumber = new SimpleStringProperty();
    private final transient StringProperty fxBuilding = new SimpleStringProperty();
    private final transient StringProperty fxFloor = new SimpleStringProperty();
    private final transient IntegerProperty fxDeviceCount = new SimpleIntegerProperty();
    private final transient StringProperty fxStatus = new SimpleStringProperty();
    private final transient StringProperty fxRoomType = new SimpleStringProperty();
    private final transient StringProperty fxDisplayRoomType = new SimpleStringProperty();
    private final transient StringProperty formattedCreatedAt = new SimpleStringProperty();
    private final transient StringProperty lastUpdated = new SimpleStringProperty();

    private final transient DoubleProperty temperature = new SimpleDoubleProperty(0);
    private final transient DoubleProperty humidity = new SimpleDoubleProperty(0);
    private final transient DoubleProperty lux = new SimpleDoubleProperty(0);
    private final transient DoubleProperty co2 = new SimpleDoubleProperty(0);

    private transient List<Device> devicesInRoom = List.of();

    public Classroom() { /*setCreationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));*/ }

    public void postProcess() {
        fxId.set(code);
        fxRoomNumber.set(name);
        fxBuilding.set(building);
        fxFloor.set(floor);
        fxDeviceCount.set(deviceCount);
        fxStatus.set(status);
        fxRoomType.set(roomType);

        formatCreatedAt(createdAt);

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
            DeviceDataMerged latest = merged.get(0);
            setTemperature(latest.getTemperature() != null ? latest.getTemperature() : 0);
            setHumidity(latest.getHumidity() != null ? latest.getHumidity() : 0);
            setLux(latest.getLux() != null ? latest.getLux() : 0);
            setCo2(latest.getCo2() != null ? latest.getCo2() : 0);
            setLastUpdated(latest.getCreatedAt());
        }
    }

    private void formatCreatedAt(String dateStr) {

        if (dateStr == null || dateStr.isEmpty()) {
            formattedCreatedAt.set("");
            return;
        }
        try {
            String formatted;
            if (dateStr.contains("T")) {
                LocalDate parsed = LocalDate.parse(dateStr.substring(0, 10)); // "2025-08-07"
                formatted = parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                System.out.println(">> SET creationDate = " + formatted);
            } else {
                LocalDate local = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                formatted = local.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                System.out.println(">> SET creationDate = " + formatted);
            }
            formattedCreatedAt.set(formatted);
        } catch (Exception e) {
            formattedCreatedAt.set(dateStr); // fallback
        }


    }

    // =============== Properties cho TableView ===============
    public StringProperty idProperty() { return fxId; }
    public StringProperty roomNumberProperty() { return fxRoomNumber; }
    public StringProperty buildingProperty() { return fxBuilding; }
    public StringProperty floorProperty() { return fxFloor; }
    public IntegerProperty deviceCountProperty() { return fxDeviceCount; }
    public StringProperty statusProperty() { return fxStatus; }
    public StringProperty roomTypeProperty() { return fxRoomType; }
    public StringProperty displayRoomTypeProperty() { return fxDisplayRoomType; }
    public StringProperty formattedCreatedAtProperty() { return formattedCreatedAt; }
    public StringProperty lastUpdatedProperty() { return lastUpdated; }

    // =============== Getters / Setters ===============
    public long getClassroomId() { return id; }
    public String getId() { return code; }
    public String getRoomNumber() { return name; }
    public String getStatus() { return status; }
    public String getRoomType() { return roomType; }
    public String getDisplayRoomType() { return displayRoomType; }

    public List<Device> getDevicesInRoom() { return devicesInRoom; }
    public void setDevicesInRoom(List<Device> devices) { this.devicesInRoom = devices; }

    public String getFormattedCreatedAt() { return formattedCreatedAt.get(); }

    public String getCreationDate() { return createdAt; }

    public void setCreationDate(String date) {
        System.out.println(">> SET creationDate = " + date);
        this.createdAt = date;
        formatCreatedAt(date);
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
        this.fxStatus.set(newStatus);
    }

    public void setLastUpdated(String value) {
        this.lastUpdated.set(value);
    }

    public String getLastUpdated() {
        return lastUpdated.get();
    }

    // =============== Sensor ===============
    public double getTemperature() { return temperature.get(); }
    public double getHumidity() { return humidity.get(); }
    public double getLux() { return lux.get(); }
    public double getCo2() { return co2.get(); }

    public void setTemperature(double temp) { this.temperature.set(temp); }
    public void setHumidity(double hum) { this.humidity.set(hum); }
    public void setLux(double lux) { this.lux.set(lux); }
    public void setCo2(double co2) { this.co2.set(co2); }

    public String getFormattedStatus() {
        return switch (status) {
            case "ACTIVE" -> "Hoạt động";
            case "INACTIVE", "MAINTENANCE" -> "Tạm ngưng";
            default -> status;
        };
    }

    public void setRoomType(String type) {
        this.roomType = type;
        this.fxRoomType.set(type); // đồng bộ luôn nếu cần
    }

    public void setRoomNumber(String roomNumber) {
        this.name = roomNumber;
        this.fxRoomNumber.set(roomNumber);
    }
}
