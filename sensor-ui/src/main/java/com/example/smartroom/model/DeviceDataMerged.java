package com.example.smartroom.model;

public class DeviceDataMerged {
    private String createdAt; // yyyy-MM-dd HH:mm
    private Double temperature;
    private Double humidity;
    private Double co2;
    private Double lux;

    public DeviceDataMerged() {}

    public DeviceDataMerged(String createdAt, Double temperature, Double humidity, Double co2, Double lux) {
        this.createdAt = createdAt;
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.lux = lux;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getCo2() {
        return co2;
    }

    public void setCo2(Double co2) {
        this.co2 = co2;
    }

    public Double getLux() {
        return lux;
    }

    public void setLux(Double lux) {
        this.lux = lux;
    }

    public String getQuality() {
        try {
            boolean tempOk = temperature != null && temperature >= 18 && temperature <= 30;
            boolean humOk = humidity != null && humidity >= 35 && humidity <= 70;
            boolean co2Ok = co2 != null && co2 > 0 && co2 <= 1500;
            boolean luxOk = lux != null && lux >= 200 && lux <= 1500;

            if (tempOk && humOk && co2Ok && luxOk) return "Tốt";
            if (!tempOk || !humOk || !co2Ok || !luxOk) return "Kém";
            return "Khá";
        } catch (Exception e) {
            return "Không rõ";
        }
    }
}
