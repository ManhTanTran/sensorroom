package com.example.smartroom.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AlertHistory {

    // Các trường tạm để Gson deserialize
    @SerializedName("deviceCode")
    private String rawDeviceId;

    @SerializedName("alertType")
    private String rawAlertType;

    @SerializedName("warning")
    private String rawWarning;

    @SerializedName("time") // giả sử backend gửi key là "time"
    private String rawTime;

    // Các JavaFX properties thực sự
    private final StringProperty time = new SimpleStringProperty();
    private final StringProperty deviceId = new SimpleStringProperty();
    private final StringProperty alertType = new SimpleStringProperty();
    private final StringProperty warning = new SimpleStringProperty();

    // ✅ Constructor để dùng trong Java code (khi tự tạo object)
    public AlertHistory(String time, String deviceId, String alertType, String warning) {
        this.time.set(time);
        this.deviceId.set(deviceId);
        this.alertType.set(alertType);
        this.warning.set(warning);
    }

    // ✅ Constructor mặc định để Gson khởi tạo object trống rồi set raw fields
    public AlertHistory() {}

    // ✅ Gọi sau khi deserialize để đẩy raw → Property
    public void postProcess() {
        this.time.set(rawTime);
        this.deviceId.set(rawDeviceId);
        this.alertType.set(rawAlertType);
        this.warning.set(rawWarning);
    }

    // Getters cho Property
    public StringProperty timeProperty() { return time; }
    public StringProperty deviceIdProperty() { return deviceId; }
    public StringProperty alertTypeProperty() { return alertType; }
    public StringProperty warningProperty() { return warning; }

    // Getter tiện dụng
    public String getCreatedAt() {
        return time.get();
    }
}
