package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.CreateClassroomRequest;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.UpdateDeviceRequest;
import com.example.smartroom.service.ApiService;
import com.example.smartroom.service.DataService;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ClassroomCreationView {

    // Callback để báo cho view cha biết khi nào cần quay lại
    private final Runnable onBackCallback;
    private final ApiService apiService = new ApiService();

    public ClassroomCreationView(Runnable onBackCallback) {
        this.onBackCallback = onBackCallback;
    }

    public Parent getView() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // --- Header với nút Lưu và Hủy ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_RIGHT);

        Button saveButton = new Button("LƯU");
        saveButton.getStyleClass().add("create-button");

        Button cancelButton = new Button("HỦY");
        cancelButton.getStyleClass().add("back-button"); // Tái sử dụng style nút quay lại
        cancelButton.setOnAction(e -> onBackCallback.run()); // Quay lại khi bấm Hủy

        header.getChildren().addAll(saveButton, cancelButton);
        mainLayout.setTop(header);

        // --- Form nhập liệu ---
        VBox content = new VBox(25);
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(30);
        inputGrid.setVgap(15);

        TextField idField = new TextField();
        TextField numberField = new TextField();
        ComboBox<String> buildingCombo = new ComboBox<>();
        buildingCombo.getItems().addAll("Toà HA8", "Toà HA9");
        ComboBox<String> floorCombo = new ComboBox<>();
        floorCombo.getItems().addAll("Tầng 1", "Tầng 2");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Phòng Thường", "Phòng Lab");
        typeCombo.getSelectionModel().selectFirst();
        // THAY ĐỔI: Loại cảm biến giờ đây khớp với Enum của backend
        ComboBox<Device> tempSensorCombo = createSensorComboBox("TEMPERATURE");
        ComboBox<Device> humiditySensorCombo = createSensorComboBox("HUMIDITY");
        ComboBox<Device> lightSensorCombo = createSensorComboBox("LIGHT");
        ComboBox<Device> co2SensorCombo = createSensorComboBox("CO2");

        inputGrid.add(new Label("Mã phòng học *"), 0, 0);
        inputGrid.add(idField, 1, 0);
        inputGrid.add(new Label("Số phòng *"), 0, 1);
        inputGrid.add(numberField, 1, 1);
        inputGrid.add(new Label("Tòa nhà *"), 0, 2);
        inputGrid.add(buildingCombo, 1, 2);
        inputGrid.add(new Label("Tầng *"), 0, 3);
        inputGrid.add(floorCombo, 1, 3);
        inputGrid.add(new Label("Loại phòng học *"), 5, 0);
        inputGrid.add(typeCombo, 6, 0);
        inputGrid.add(new Label("Nhiệt độ *"), 5, 1);
        inputGrid.add(tempSensorCombo, 6, 1);
        inputGrid.add(new Label("Độ ẩm *"), 7, 1);
        inputGrid.add(humiditySensorCombo, 8, 1);
        inputGrid.add(new Label("Ánh sáng *"), 5, 2);
        inputGrid.add(lightSensorCombo, 6, 2);
        inputGrid.add(new Label("CO2 *"), 7, 2);
        inputGrid.add(co2SensorCombo, 8, 2);


        inputGrid.add(new Label("Ghi chú"), 5, 3);
        TextField notesArea = new TextField();
        notesArea.setMinHeight(50);
        inputGrid.add(notesArea, 6, 3);

        StackPane layoutContainer = new StackPane();
        Runnable updateSensorButtons = () -> {
            List<Device> selectedDevices = new ArrayList<>();
            if (tempSensorCombo.getValue() != null) selectedDevices.add(tempSensorCombo.getValue());
            if (humiditySensorCombo.getValue() != null) selectedDevices.add(humiditySensorCombo.getValue());
            if (lightSensorCombo.getValue() != null) selectedDevices.add(lightSensorCombo.getValue());
            if (co2SensorCombo.getValue() != null) selectedDevices.add(co2SensorCombo.getValue());

            Node newLayout = "Phòng Lab".equals(typeCombo.getValue())
                    ? ClassroomLayouts.createLabClassroomLayout(selectedDevices)
                    : ClassroomLayouts.createRegularClassroomLayout(selectedDevices);
            layoutContainer.getChildren().setAll(newLayout);
        };
        layoutContainer.getChildren().add(ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>()));

        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            layoutContainer.getChildren().clear();
            if ("Phòng Lab".equals(newVal)) {
                layoutContainer.getChildren().add(ClassroomLayouts.createLabClassroomLayout(new ArrayList<>()));
            } else {
                layoutContainer.getChildren().add(ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>()));
            }
        });

        typeCombo.setOnAction(e -> updateSensorButtons.run());
        tempSensorCombo.setOnAction(e -> updateSensorButtons.run());
        humiditySensorCombo.setOnAction(e -> updateSensorButtons.run());
        lightSensorCombo.setOnAction(e -> updateSensorButtons.run());
        co2SensorCombo.setOnAction(e -> updateSensorButtons.run());

        Label inputLabel = createHeaderLabel("Thông tin chung");
        Label layoutLabel = createHeaderLabel("Sơ đồ mô phỏng phòng học");
        content.getChildren().addAll(inputLabel, inputGrid, new Separator(), layoutLabel, layoutContainer);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10, 0, 10, 0));
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);
        BorderPane.setMargin(content, new Insets(20, 0, 0, 0));

        // --- LOGIC LƯU TRỮ (ĐÃ SỬA) ---
        saveButton.setOnAction(e -> {
            String code = idField.getText();
            String name = numberField.getText();
            String building = buildingCombo.getValue();
            String floor = floorCombo.getValue();
            String roomType = typeCombo.getValue();
            String mappedRoomType = roomType.equals("Phòng Lab") ? "LAB" : "THUONG";

            String note = notesArea.getText();
            Device temp = tempSensorCombo.getValue();
            Device humidity = humiditySensorCombo.getValue();
            Device co2 = co2SensorCombo.getValue();
            Device lux = lightSensorCombo.getValue();

            if (code.isEmpty() || name.isEmpty() || building == null || floor == null || roomType == null
                    || temp == null || humidity == null || co2 == null || lux == null) {
                new Alert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ các trường bắt buộc (*).").show();
                return;
            }

            CreateClassroomRequest newClassroomRequest = new CreateClassroomRequest(code, name, building, floor, mappedRoomType, note);

            String json = new Gson().toJson(newClassroomRequest);
            System.out.println(">>> JSON gửi đi:");
            System.out.println(json);

            saveButton.setDisable(true);
            cancelButton.setDisable(true);

            apiService.createClassroom(newClassroomRequest)
                    .thenCompose(createdClassroom -> {
                        // ✅ Format lại ngày tạo
                        //String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        //System.out.println(today);
                        //createdClassroom.setCreationDate(today);

                        createdClassroom.setCreationDate(createdClassroom.getCreationDate()); // ép re-format
                        createdClassroom.postProcess(); // cập nhật các property

                        System.out.println("⏳ formatted: " + createdClassroom.getFormattedCreatedAt());


                        List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

                        updateDeviceFuture(updateFutures, tempSensorCombo.getValue(), createdClassroom.getClassroomId());
                        updateDeviceFuture(updateFutures, humiditySensorCombo.getValue(), createdClassroom.getClassroomId());
                        updateDeviceFuture(updateFutures, lightSensorCombo.getValue(), createdClassroom.getClassroomId());
                        updateDeviceFuture(updateFutures, co2SensorCombo.getValue(), createdClassroom.getClassroomId());

                        return CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0]));
                    })
                    .thenRun(() -> {
                        Platform.runLater(() -> {
                            new Alert(Alert.AlertType.INFORMATION, "Đã tạo và gán thiết bị cho phòng học mới thành công!").show();
                            onBackCallback.run();

                            //new ClassroomManagementView ().refreshView();
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            ex.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Lỗi khi tạo phòng: " + ex.getCause().getMessage()).show();
                            saveButton.setDisable(false);
                            cancelButton.setDisable(false);
                        });
                        return null;
                    });
        });

        return mainLayout;
    }

    private void updateDeviceFuture(List<CompletableFuture<Void>> futures, Device device, long classroomId) {
        if (device != null) {
            UpdateDeviceRequest updateReq = new UpdateDeviceRequest(
                    device.getName(),
                    device.getType(),
                    "ACTIVE",
                    device.getDataCycle() != null ? device.getDataCycle() : 60,
                    device.getNotes() != null ? device.getNotes() : "",
                    classroomId
            );
            futures.add(apiService.updateDevice(device.getDeviceId(), updateReq));
        }
    }


    private ComboBox<Device> createSensorComboBox(String type) {
        ComboBox<Device> comboBox = new ComboBox<>();
        comboBox.setPromptText("Chọn thiết bị");

        apiService.fetchDevices().thenAccept(devices -> {
            ObservableList<Device> availableDevices = devices.stream()
                    .filter(d -> d.getRoom() == null && type.equals(d.getType()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            Platform.runLater(() -> comboBox.setItems(availableDevices));
        });
        comboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Device device) { return device == null ? "" : device.imeiProperty().get(); }
            @Override public Device fromString(String string) { return null; }
        });

        return comboBox;
    }

    private void updateDevice(Device device, String roomId) {
        if (device != null) {
            device.roomProperty().set(roomId);
            device.statusProperty().set("Hoạt động");
        }
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("table-title");
        return label;
    }
}