package com.example.smartroom.model;

public class UpdateDeviceRequest {
    private String classroomId; // Backend có thể mong đợi mã phòng thay vì id số
    private String status;

    public UpdateDeviceRequest(String classroomId, String status) {
        this.classroomId = classroomId;
        this.status = status;
    }

    // Getters and Setters
    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}