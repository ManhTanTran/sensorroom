package com.example.smartroom.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AlertHistory {
    private final StringProperty time;
    private final StringProperty deviceName;
    private final StringProperty alertType;
    private final StringProperty warning;

    public AlertHistory(String time, String deviceName, String alertType, String warning) {
        this.time = new SimpleStringProperty(time);
        this.deviceName = new SimpleStringProperty(deviceName);
        this.alertType = new SimpleStringProperty(alertType);
        this.warning = new SimpleStringProperty(warning);
    }

    public StringProperty timeProperty() { return time; }
    public StringProperty deviceNameProperty() { return deviceName; }
    public StringProperty alertTypeProperty() { return alertType; }
    public StringProperty warningProperty() { return warning; }
}