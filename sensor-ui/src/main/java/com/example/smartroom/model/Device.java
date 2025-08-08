package com.example.smartroom.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Device {

    // --- Data fields từ backend ---
    private Long id;
    private String classroomName;
    private String createdByName;
    private String name;
    private String deviceCode;
    private String type;
    private String status;
    private Integer dataCycle;
    private String notes;
    private Long classroomId;
    private Long createdById;
    private LocalDateTime createdAt;

    // --- JavaFX properties để binding TableView ---
    private transient StringProperty fxId;
    private transient StringProperty fxRoom;
    private transient StringProperty fxImei;
    private transient StringProperty fxType;
    private transient StringProperty fxStatus;
    private transient StringProperty fxCreatedAt;
    private transient StringProperty fxValue;
    private transient StringProperty fxLastActive;

    public Device() {}

    // Gọi hàm này sau khi parse JSON từ API để binding lên bảng
    public void postProcess() {
        this.fxId = new SimpleStringProperty(id != null ? String.valueOf(id) : "-");
        this.fxRoom = new SimpleStringProperty(classroomName != null ? classroomName : "-");
        this.fxImei = new SimpleStringProperty(deviceCode != null ? deviceCode : "-");
        this.fxType = new SimpleStringProperty(getFormattedType());
        this.fxStatus = new SimpleStringProperty(getFormattedStatus());

        if (createdAt != null) {
            String formatted = createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.fxCreatedAt = new SimpleStringProperty(formatted);
        } else {
            this.fxCreatedAt = new SimpleStringProperty("-");
        }

        this.fxValue = new SimpleStringProperty("-"); // sẽ cập nhật sau
    }

    // --- JavaFX Property Getters (bắt buộc để TableView hiển thị) ---
    public StringProperty idProperty() { return fxId; }
    public StringProperty imeiProperty() { return fxImei; }
    public StringProperty typeProperty() { return fxType; }
    public StringProperty roomProperty() { return fxRoom; }
    public StringProperty valueProperty() { return fxValue; }
    public StringProperty lastActiveProperty() {
        if (fxLastActive == null) fxLastActive = new SimpleStringProperty("-");
        return fxLastActive;
    }
    public StringProperty statusProperty() { return fxStatus; }

    // --- Basic getter để xử lý logic ---
    public long getId() { return id != null ? id : -1; }
    public String getName() { return name; }
    public Long getClassroomId() { return classroomId; }
    public String getRoom() { return classroomName; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getDeviceId() { return deviceCode; }
    public String getRawType() {
        return this.type;
    }
    public Integer getDataCycle() { return dataCycle; }
    public String getNotes () { return notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getFormattedStatus() {
        return switch (status) {
            case "ACTIVE" -> "Hoạt động";
            case "INACTIVE" -> "Tạm ngưng";
            case "ERROR" -> "Mất kết nối";
            default -> "Không rõ";
        };
    }

    public String getFormattedType() {
        return switch (type) {
            case "TEMPERATURE" -> "Cảm biến nhiệt độ";
            case "HUMIDITY" -> "Cảm biến độ ẩm";
            case "LIGHT" -> "Cảm biến ánh sáng";
            case "CO2" -> "Cảm biến CO2";
            default -> "Khác";
        };
    }

    // --- Update thông số đo ---
    public void setValue(String value) {
        if (this.fxValue == null) this.fxValue = new SimpleStringProperty();
        this.fxValue.set(value);
    }

    public void setLastActive(String value) {
        if (fxLastActive == null) fxLastActive = new SimpleStringProperty();
        fxLastActive.set(value);
    }

    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
}
