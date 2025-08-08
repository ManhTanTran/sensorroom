package com.example.smartroom.model;

public class UpdateClassroomRequest {
    private String name;
    private String building;
    private String floor;
    private Roomtype roomtype;

    public UpdateClassroomRequest(String name, String building, String floor, Roomtype roomtype) {
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.roomtype = roomtype;
    }
}
