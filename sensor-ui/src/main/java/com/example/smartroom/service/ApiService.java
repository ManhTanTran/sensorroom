package com.example.smartroom.service;

import com.example.smartroom.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .create();

    public CompletableFuture<ObservableList<Device>> fetchDevices() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/devices"))
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(body -> {
                    Type listType = new TypeToken<List<Device>>() {}.getType();
                    List<Device> devices = gson.fromJson(body, listType);
                    devices.forEach(Device::postProcess);
                    return FXCollections.observableArrayList(devices);
                });
    }

    public CompletableFuture<ObservableList<Classroom>> fetchClassrooms() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/classrooms"))
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(body -> {
                    Type listType = new TypeToken<List<Classroom>>() {}.getType();
                    List<Classroom> classrooms = gson.fromJson(body, listType);
                    classrooms.forEach(Classroom::postProcess);
                    return FXCollections.observableArrayList(classrooms);
                });
    }

    public CompletableFuture<List<DeviceData>> fetchAllDeviceData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/device-data"))
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(body -> {
                    Type listType = new TypeToken<List<DeviceData>>() {}.getType();
                    return gson.fromJson(body, listType);
                });
    }

    public List<Device> fetchDevicesByClassroomIdSync(long classroomId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices?classroomId=" + classroomId))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = handleResponse(response);

            Type listType = new TypeToken<List<Device>>() {}.getType();
            List<Device> devices = gson.fromJson(body, listType);
            devices.forEach(Device::postProcess);
            return devices;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // hoặc throw nếu muốn fail loud
        }
    }


    public CompletableFuture<Void> deleteDevice(long deviceId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/devices/" + deviceId))
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenAccept(body -> {});
    }

    private String handleResponse(HttpResponse<String> response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Lỗi khi gọi API. Status code: " + response.statusCode());
        }
    }

    public CompletableFuture<Classroom> createClassroom(CreateClassroomRequest request) {
        String json = gson.toJson(request); // Serialize đối tượng thành JSON

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/classrooms"))  // <-- đảm bảo URL đúng
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(body -> gson.fromJson(body, Classroom.class));
    }

    public CompletableFuture<Void> deleteClassroom(long classroomId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/classrooms/" + classroomId))
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200 && response.statusCode() != 204) {
                        throw new RuntimeException("Xóa phòng học thất bại: " + response.body());
                    }
                    return null;
                });
    }

    public CompletableFuture<Void> updateClassroom(Classroom classroom) {
        String url = BASE_URL + "/classrooms/" + classroom.getClassroomId();
        Roomtype mappedEnum;
        try {
            mappedEnum = Roomtype.valueOf(classroom.getRoomType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Roomtype không hợp lệ: " + classroom.getRoomType());
        }

        UpdateClassroomRequest dto = new UpdateClassroomRequest(
                classroom.getRoomNumber(),
                classroom.buildingProperty().get(),
                classroom.floorProperty().get(),
               mappedEnum
        );

        Gson gson = new Gson();
        String requestBody = gson.toJson(dto);
        System.out.println("JSON gửi lên: " + requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("STATUS: " + response.statusCode());
                    System.out.println("BODY: " + response.body());
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Cập nhật classroom thất bại: " + response.body());
                    }
                });
    }




    /**
     * THAY ĐỔI: Sửa lại phương thức này để nhận deviceId kiểu long, khớp với backend
     */
    public CompletableFuture<Void> updateDevice(String deviceCode, UpdateDeviceRequest updateRequest) {
        String requestBody = gson.toJson(updateRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/devices/code/" + deviceCode)) // URL giờ đây đúng
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenAccept(body -> {});
    }

    private <T> ObservableList<T> parseJsonList(String jsonBody, TypeToken<T> typeToken) {
        try {
            Type listType = typeToken.getType();
            List<T> list = gson.fromJson(jsonBody, listType);

            // Xử lý sau khi parse
            if (!list.isEmpty()) {
                if (list.get(0) instanceof Device) {
                    ((List<Device>) list).forEach(Device::postProcess);
                } else if (list.get(0) instanceof Classroom) {
                    ((List<Classroom>) list).forEach(Classroom::postProcess);
                }
            }
            return FXCollections.observableArrayList(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể phân tích dữ liệu JSON từ server.", e);
        }
    }

    private <T> T parseJsonObject(String jsonBody, TypeToken<T> typeToken) {
        try {
            Type type = typeToken.getType();
            T object = gson.fromJson(jsonBody, type);

            // Xử lý sau khi parse
            if (object instanceof Classroom) {
                ((Classroom) object).postProcess();
            } else if (object instanceof Device) {
                ((Device) object).postProcess();
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể phân tích dữ liệu JSON đơn lẻ từ server.", e);
        }
    }
}