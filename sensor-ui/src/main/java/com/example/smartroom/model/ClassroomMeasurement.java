package com.example.smartroom.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClassroomMeasurement {
    private final StringProperty humidity;
    private final StringProperty temperature;
    private final StringProperty lux;
    private final StringProperty co2;
    private final StringProperty time;
    private final StringProperty quality;

    public ClassroomMeasurement(String humidity, String temperature, String lux, String co2, String time, String quality) {
        this.humidity = new SimpleStringProperty(humidity);
        this.temperature = new SimpleStringProperty(temperature);
        this.lux = new SimpleStringProperty(lux);
        this.co2 = new SimpleStringProperty(co2);
        this.time = new SimpleStringProperty(time);
        this.quality = new SimpleStringProperty(quality);
    }

    public StringProperty humidityProperty() { return humidity; }
    public StringProperty temperatureProperty() { return temperature; }
    public StringProperty luxProperty() { return lux; }
    public StringProperty co2Property() { return co2; }
    public StringProperty timeProperty() { return time; }
    public StringProperty qualityProperty() { return quality; }
}