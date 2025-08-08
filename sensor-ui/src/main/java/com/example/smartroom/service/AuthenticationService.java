package com.example.smartroom.service;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;

public class AuthenticationService {
    // Giả sử ta tạo trước một số Classroom mẫu
    private static final Classroom ROOM_101A;
    private static final Classroom ROOM_201A;
    private static final Classroom ROOM_201B;
    private static final Classroom ROOM_103B;
    private static final Classroom ROOM_102A;

    static {
        ROOM_101A = new Classroom();
        ROOM_101A.setRoomNumber("101 A");

        ROOM_201A = new Classroom();
        ROOM_201A.setRoomNumber("201 A");

        ROOM_201B = new Classroom();
        ROOM_201B.setRoomNumber("201 B");

        ROOM_103B = new Classroom();
        ROOM_103B.setRoomNumber("103 B");

        ROOM_102A = new Classroom();
        ROOM_102A.setRoomNumber("102 A");
    }

    private final List<User> users = Arrays.asList(
            new User("admin", "admin", "Admin", Role.ADMIN, List.of()),
            // KTV quản lý nhiều phòng
            new User("ktv_multi", "ktv", "Nguyen Van A", Role.KTV, List.of(ROOM_102A, ROOM_201A, ROOM_201B)),
            // KTV chỉ quản lý 1 phòng
            new User("ktv_single", "ktv", "Tran Thi B", Role.KTV, List.of(ROOM_101A))
    );

    public User login(String username, String password) {
        User loggedInUser = users.stream()
                .filter(user -> user.username().equals(username) && user.password().equals(password))
                .findFirst()
                .orElse(null);

        if (loggedInUser != null) {
            // 🔹 Lấy classroom đầy đủ từ API
            ObservableList<Classroom> classrooms = DataService.getClassroomsForKtvFromApi(loggedInUser.managedRooms());
            DataService.setAllClassrooms(classrooms);

            // 🔹 Lấy thiết bị của các phòng này
            ObservableList<Device> devices = DataService.getDevicesForKtv(loggedInUser.managedRooms());
            DataService.setAllDevices(devices);
        }

        return loggedInUser;
    }
}