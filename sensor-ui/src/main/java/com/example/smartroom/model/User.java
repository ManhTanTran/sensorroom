package com.example.smartroom.model;

import java.util.List;

public record User(String username, String password, String fullName, Role role, List<Classroom> managedRooms) {}