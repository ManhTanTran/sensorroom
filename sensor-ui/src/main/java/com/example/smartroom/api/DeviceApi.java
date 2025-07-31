package com.example.smartroom.api;

import com.example.smartroom.model.Device;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceApi {
    public static List<Device> fetchDevicesFromApi() {
        List<Device> deviceList = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8080/api/devices"); // đổi lại nếu dùng port khác
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                ObjectMapper mapper = new ObjectMapper(); // Dùng Jackson
                deviceList = Arrays.asList(mapper.readValue(reader, Device[].class));
                reader.close();
            } else {
                System.err.println("Failed to fetch devices. Response code: " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deviceList;
    }
}
