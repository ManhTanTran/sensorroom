
package com.example.smartroom.service;


import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.google.gson.Gson; // SỬ DỤNG GSON
import com.google.gson.reflect.TypeToken; // SỬ DỤNG GSON
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Type; // Cần cho TypeToken
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/swagger-ui/index.html#/";

    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson = new Gson();

    public CompletableFuture<ObservableList<Device>> fetchAllDevices() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/devices"))
                .header("Accept", "application/json") // Báo cho server biết chúng ta muốn nhận JSON
                .GET() // Chỉ định phương thức là GET
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse) // Kiểm tra status code
                .thenApply(this::parseDeviceListFromJson) // Phân tích chuỗi JSON thành List<Device>
                .thenApply(FXCollections::observableArrayList); // Chuyển List thành ObservableList
    }

    public CompletableFuture<ObservableList<Classroom>> fetchAllClassrooms() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/classrooms"))
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(this::parseClassroomListFromJson)
                .thenApply(FXCollections::observableArrayList);
    }

    private String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        } else {
            // Ném ra lỗi để chuỗi .exceptionally() có thể bắt được
            throw new RuntimeException("Lỗi khi gọi API. Status code: " + statusCode + ", Body: " + response.body());
        }
    }

    private List<Device> parseDeviceListFromJson(String jsonBody) {
        try {
            // TypeToken giúp GSON biết rằng chúng ta muốn parse thành một List<Device>
            Type deviceListType = new TypeToken<List<Device>>() {}.getType();
            return gson.fromJson(jsonBody, deviceListType);
        } catch (Exception e) {
            e.printStackTrace();
            // Ném ra lỗi để chuỗi .exceptionally() có thể bắt được
            throw new RuntimeException("Không thể phân tích dữ liệu JSON Device từ server.", e);
        }
    }

    private List<Classroom> parseClassroomListFromJson(String jsonBody) {
        try {
            Type classroomListType = new TypeToken<List<Classroom>>() {}.getType();
            return gson.fromJson(jsonBody, classroomListType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể phân tích dữ liệu JSON Classroom từ server.", e);
        }
    }
}
