package com.example.smartroom.view;

import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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

        Device sensor1 = findDeviceByType(devicesInRoom, "TEMPERATURE");
        Device sensor2 = findDeviceByType(devicesInRoom, "HUMIDITY");
        Device sensor3 = findDeviceByType(devicesInRoom, "LIGHT");
        Device sensor4 = findDeviceByType(devicesInRoom, "CO2");


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

        Device sensor1 = findDeviceByType(devicesInRoom, "TEMPERATURE");
        Device sensor2 = findDeviceByType(devicesInRoom, "HUMIDITY");
        Device sensor3 = findDeviceByType(devicesInRoom, "LIGHT");
        Device sensor4 = findDeviceByType(devicesInRoom, "CO2");


        layout.getChildren().add(createSensorButton(sensor1, 525, 300));
        layout.getChildren().add(createSensorButton(sensor2, 850, 80));
        layout.getChildren().add(createSensorButton(sensor3, 180, 70));
        layout.getChildren().add(createSensorButton(sensor4, 910, 200));

        return layout;
    }

    private static Device findDeviceByType(List<Device> devices, String rawType) {
        if (devices == null) return null;
        return devices.stream()
                .filter(d -> rawType.equals(d.getRawType()))
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
        sensorButton.getStyleClass().add("sensor-button");
        sensorButton.setCursor(Cursor.HAND);

        if (device != null) {
            sensorButton.setDisable(false);

            sensorButton.setOnAction(e -> {
                // Lấy bản cập nhật mới nhất từ danh sách toàn cục
                Optional<Device> latest = DataService.getAllDevices().stream()
                        .filter(d -> d.getDeviceId().equals(device.getDeviceId()))
                        .findFirst();

                String valueStr = latest.map(d -> d.valueProperty().get()).orElse("-");
                String evaluation = "Không xác định";

                try {
                    // Parse giá trị số từ chuỗi (bỏ đơn vị như °C, %, lux, ppm)
                    double numericValue = Double.parseDouble(
                            valueStr.replaceAll("[^\\d.]", "") // loại bỏ mọi ký tự không phải số hoặc dấu chấm
                    );

                    String type = device.getType();
                    boolean isExceeded = switch (type) {
                        case "TEMPERATURE" -> numericValue > 30 || numericValue < 18;
                        case "HUMIDITY" -> numericValue > 70 || numericValue < 35;
                        case "LIGHT" -> numericValue > 1500;
                        case "CO2" -> numericValue > 1500;
                        default -> false;
                    };

                    evaluation = isExceeded ? "Vượt ngưỡng!!!" : "Bình thường";
                } catch (Exception ex) {
                    evaluation = "Không đọc được giá trị";
                }

                String serial = device.imeiProperty().get();
                String type = device.getType();
                String room = device.getRoom();

                Text serialText = new Text("Serial: " + serial + "\n");
                Text typeText = new Text("Loại: " + type + "\n");
                Text roomText = new Text("Phòng: " + room + "\n");
                Text valueText = new Text("Thông số gần nhất: " + valueStr + "\n");

                Text evaluationText = new Text("Đánh giá: " + evaluation);
                if ("Vượt ngưỡng!!!".equals(evaluation)) {
                    evaluationText.setFill(Color.RED);
                    evaluationText.setFont(Font.font("System", FontWeight.BOLD, 16));
                } else {
                    evaluationText.setFont(Font.font("System", 14));
                }

// Tăng kích cỡ font cho các Text khác nếu muốn
                Font normalFont = Font.font("System", 14);
                serialText.setFont(normalFont);
                typeText.setFont(normalFont);
                roomText.setFont(normalFont);
                valueText.setFont(normalFont);

                TextFlow textFlow = new TextFlow(serialText, typeText, roomText, valueText, evaluationText);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông tin chi tiết cảm biến");
                alert.setHeaderText(type + " - " + room);
                alert.getDialogPane().setMinWidth(350);
                alert.getDialogPane().setContent(textFlow);
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