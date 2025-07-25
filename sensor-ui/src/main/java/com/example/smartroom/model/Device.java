package com.example.smartroom.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Device {
    private final StringProperty id;
    private final StringProperty imei;
    private final StringProperty type;
    private final StringProperty room;
    private final StringProperty value; // Thuộc tính duy nhất cho giá trị đo
    private final StringProperty lastActive;
    private final StringProperty status;

    public Device(String id, String imei, String type, String room, String value, String lastActive, String status) {
        this.id = new SimpleStringProperty(id);
        this.imei = new SimpleStringProperty(imei);
        this.type = new SimpleStringProperty(type);
        this.room = new SimpleStringProperty(room);
        this.value = new SimpleStringProperty(value);
        this.lastActive = new SimpleStringProperty(lastActive);
        this.status = new SimpleStringProperty(status);
    }

    // --- Property Getters ---
    public StringProperty idProperty() { return id; }
    public StringProperty imeiProperty() { return imei; }
    public StringProperty typeProperty() { return type; }
    public StringProperty roomProperty() { return room; }
    public StringProperty valueProperty() { return value; }
    public StringProperty lastActiveProperty() { return lastActive; }
    public StringProperty statusProperty() { return status; }

    // --- Simple Getters ---
    public String getRoom() { return room.get(); }
    public String getType() { return type.get(); }
}