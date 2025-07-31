package com.example.smartroom.view;

import com.example.smartroom.model.Device;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class ClassroomLayouts {

    public static Pane createRegularClassroomLayout(List<Device> devicesInRoom) {
        Pane layout = new Pane();
        layout.getStyleClass().add("classroom-layout");
        layout.setPrefSize(1000, 400);

        layout.getChildren().add(createClassroomObject("MÁY CHIẾU", 520, 20, 100, 20));
        layout.getChildren().add(createClassroomObject("BÀN GIÁO VIÊN", 850, 20, 150, 40));
        layout.getChildren().add(createClassroomObject("(CỬA CHÍNH)", 180, 20, 100, 40));
        layout.getChildren().add(createClassroomObject("(CỬA SỔ 1)", 910, 150, 90, 40));
        layout.getChildren().add(createClassroomObject("(CỬA SỔ 2)", 910, 250, 90, 40));
        layout.getChildren().add(createClassroomObject("ĐIỀU HÒA", 370, 320, 100, 30));
        layout.getChildren().add(createClassroomObject("ĐIỀU HÒA", 680, 320, 100, 30));
        for (int row = 0; row < 3; row++) {
            layout.getChildren().add(createClassroomObject("bàn học", 300, 100 + row * 70, 100, 40));
            layout.getChildren().add(createClassroomObject("bàn học", 450, 100 + row * 70, 100, 40));
            layout.getChildren().add(createClassroomObject("bàn học", 610, 100 + row * 70, 100, 40));
            layout.getChildren().add(createClassroomObject("bàn học", 750, 100 + row * 70, 100, 40));
        }

        Device sensor1 = findDeviceByType(devicesInRoom, "Cảm biến nhiệt độ");
        Device sensor2 = findDeviceByType(devicesInRoom, "Cảm biến độ ẩm");
        Device sensor3 = findDeviceByType(devicesInRoom, "Cảm biến ánh sáng");
        Device sensor4 = findDeviceByType(devicesInRoom, "Cảm biến CO2");

        layout.getChildren().add(createSensorButton(sensor1, 920, 80));
        layout.getChildren().add(createSensorButton(sensor2, 180, 170));
        layout.getChildren().add(createSensorButton(sensor3, 920, 320));
        layout.getChildren().add(createSensorButton(sensor4, 535, 320));

        return layout;
    }

    public static Pane createLabClassroomLayout(List<Device> devicesInRoom) {
        Pane layout = new Pane();
        layout.getStyleClass().add("classroom-layout");
        layout.setPrefSize(1000, 400);

        layout.getChildren().add(createClassroomObject("(CỬA CHÍNH)", 180, 20, 100, 40));
        layout.getChildren().add(createClassroomObject("BÀN GIÁO VIÊN", 850, 20, 150, 40));
        layout.getChildren().add(createClassroomObject("(CỬA SỔ 1)", 910, 150, 90, 40));
        layout.getChildren().add(createClassroomObject("(CỬA SỔ 2)", 910, 250, 90, 40));
        layout.getChildren().add(createClassroomObject("TỦ DỤNG CỤ", 180, 150, 100, 60));
        layout.getChildren().add(createClassroomObject("TỦ HÓA CHẤT", 180, 250, 100, 60));
        for (int i = 0; i < 3; i++) {
            layout.getChildren().add(createClassroomObject("Bàn nhóm", 330, 100 + i * 70, 150, 50));
            layout.getChildren().add(createClassroomObject("Bàn nhóm", 650, 100 + i * 70, 150, 50));
        }

        Device sensor1 = findDeviceByType(devicesInRoom, "Cảm biến nhiệt độ");
        Device sensor2 = findDeviceByType(devicesInRoom, "Cảm biến độ ẩm");
        Device sensor3 = findDeviceByType(devicesInRoom, "Cảm biến ánh sáng");
        Device sensor4 = findDeviceByType(devicesInRoom, "Cảm biến CO2");

        layout.getChildren().add(createSensorButton(sensor1, 525, 300));
        layout.getChildren().add(createSensorButton(sensor2, 850, 80));
        layout.getChildren().add(createSensorButton(sensor3, 180, 70));
        layout.getChildren().add(createSensorButton(sensor4, 910, 200));

        return layout;
    }

    private static Device findDeviceByType(List<Device> devices, String type) {
        if (devices == null) return null;
        return devices.stream()
                .filter(d -> type.equals(d.getType()))
                .findFirst()
                .orElse(null);
    }

    private static Node createClassroomObject(String name, double x, double y, double width, double height) {
        VBox box = new VBox(new Label(name));
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("classroom-object");
        box.setLayoutX(x);
        box.setLayoutY(y);
        box.setPrefSize(width, height);
        return box;
    }

    /*
    private static Node createSensorButton(Device device, double x, double y) {
        Button sensorButton = new Button("[Cảm biến]");
        sensorButton.setCursor(Cursor.HAND);

        if (device != null) {
            sensorButton.setDisable(false);

            String details = String.format(
                    "Serial: %s\nLoại: %s\nPhòng: %s\nThông số gần nhất: %s",
                    device.imeiProperty().get(),
                    device.getType(),
                    device.getRoom(),
                    device.valueProperty().get()
            );

            sensorButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông tin chi tiết cảm biến");
                alert.setHeaderText(device.getType());
                alert.setContentText(details);
                alert.getDialogPane().setMinWidth(350);
                alert.showAndWait();
            });
        } else {
            sensorButton.setDisable(true);
        }

        sensorButton.setLayoutX(x);
        sensorButton.setLayoutY(y);
        return sensorButton;
    }
    */

    private static Node createSensorButton(Device device, double x, double y) {
        Button sensorButton = new Button("[Cảm biến]");
        sensorButton.getStyleClass().add("sensor-button"); // ÁP DỤNG STYLE MỚI
        sensorButton.setCursor(Cursor.HAND);

        if (device != null) {
            sensorButton.setDisable(false);

            String details = String.format(
                    "Serial: %s\nLoại: %s\nPhòng: %s\nThông số gần nhất: %s",
                    device.imeiProperty().get(),
                    device.getType(),
                    device.getRoom(),
                    device.valueProperty().get()
            );

            sensorButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông tin chi tiết cảm biến");
                alert.setHeaderText(device.getType() + " - " + device.getRoom());
                alert.setContentText(details);
                alert.getDialogPane().setMinWidth(350);
                alert.showAndWait();
            });
        } else {
            sensorButton.setDisable(true);
        }

        sensorButton.setLayoutX(x);
        sensorButton.setLayoutY(y);
        return sensorButton;
    }
}