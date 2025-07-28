package com.example.smartroom.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Classroom {
    private final StringProperty id;
    private final StringProperty roomNumber;
    private final StringProperty building;
    private final StringProperty floor;
    private final StringProperty roomType;
    private final ReadOnlyIntegerWrapper deviceCount; // Sửa thành ReadOnlyIntegerWrapper để binding
    private final StringProperty creationDate;
    private final StringProperty status;

    private final DoubleProperty temperature;
    private final DoubleProperty humidity;
    private final DoubleProperty lux;
    private final IntegerProperty co2;

    public Classroom(String id, String roomNumber, String building, String floor, String roomType, String creationDate, String status,
                     double temperature, double humidity, double lux, int co2, ObservableList<Device> masterDeviceList) {
        this.id = new SimpleStringProperty(id);
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.building = new SimpleStringProperty(building);
        this.floor = new SimpleStringProperty(floor);
        this.roomType = new SimpleStringProperty(roomType);
        this.creationDate = new SimpleStringProperty(creationDate);
        this.status = new SimpleStringProperty(status);
        this.temperature = new SimpleDoubleProperty(temperature);
        this.humidity = new SimpleDoubleProperty(humidity);
        this.lux = new SimpleDoubleProperty(lux);
        this.co2 = new SimpleIntegerProperty(co2);

        // --- LOGIC CẬP NHẬT SỐ LƯỢNG THIẾT BỊ ĐỘNG ---
        this.deviceCount = new ReadOnlyIntegerWrapper();
        this.deviceCount.bind(Bindings.createIntegerBinding(() ->
                        (int) masterDeviceList.stream().filter(d -> this.getId().equals(d.getRoom())).count(),
                masterDeviceList // Lắng nghe sự thay đổi trên danh sách này
        ));
    }

    public ReadOnlyIntegerProperty deviceCountProperty() { return deviceCount.getReadOnlyProperty(); }
    public StringProperty idProperty() { return id; }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public StringProperty buildingProperty() { return building; }
    public StringProperty floorProperty() { return floor; }
    public StringProperty roomTypeProperty() { return roomType; }
    public StringProperty creationDateProperty() { return creationDate; }
    public StringProperty statusProperty() { return status; }
    public String getId() { return id.get(); }
    public String getRoomNumber() { return roomNumber.get(); }
    public String getBuilding() { return building.get(); }
    public double getTemperature() { return temperature.get(); }
    public double getHumidity() { return humidity.get(); }
    public double getLux() { return lux.get(); }
    public int getCo2() { return co2.get(); }
    public void setStatus(String status) { this.status.set(status); }
}