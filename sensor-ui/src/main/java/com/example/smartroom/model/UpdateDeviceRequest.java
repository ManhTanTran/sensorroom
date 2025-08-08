package com.example.smartroom.model;

public class UpdateDeviceRequest {
    private String name;
    private String type;
    private String status;
    private Integer dataCycle;
    private String notes;
    private Long classroomId;

    public UpdateDeviceRequest() {}

    public UpdateDeviceRequest(String name, String type, String status, Integer dataCycle, String notes, Long classroomId) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.dataCycle = dataCycle;
        this.notes = notes;
        this.classroomId = classroomId;
    }

    // Getters and setters...
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDataCycle() { return dataCycle; }
    public void setDataCycle(Integer dataCycle) { this.dataCycle = dataCycle; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(long classroomId) { this.classroomId = classroomId; }
}
