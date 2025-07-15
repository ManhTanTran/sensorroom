package com.example.sensorroom.dto;

public class SensorData {
    private int classroomId;
    private double temperature;
    private double humidity;
    private double co2;

    public SensorData(int classroomId, double temperature, double humidity, double co2) {
        this.classroomId = classroomId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getCo2() {
        return co2;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }
}
