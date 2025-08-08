package com.example.smartroom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateClassroomRequest {
    private String code;
    private String name;
    private String building;
    private String floor;
    private String note;
    private String roomtype;

    private String status = "ACTIVE";

    public CreateClassroomRequest(String code, String name, String building, String floor, String roomtype, String note) {
        this.code = code;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.roomtype = roomtype;
        this.note = note;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getRoomType() { return roomtype; }
    public void setRoomType(String roomtype) { this.roomtype = roomtype; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
