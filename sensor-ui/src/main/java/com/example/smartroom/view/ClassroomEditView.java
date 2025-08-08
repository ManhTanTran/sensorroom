package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.Roomtype;
import com.example.smartroom.model.UpdateDeviceRequest;
import com.example.smartroom.service.ApiService;
import com.example.smartroom.service.DataService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class ClassroomEditView {
    private final Runnable onBackCallback;
    private final Classroom classroomToEdit;

    public ClassroomEditView(Runnable onBackCallback, Classroom classroomToEdit) {
        this.onBackCallback = onBackCallback;
        this.classroomToEdit = classroomToEdit;
    }

    public Parent getView() {
        String oldRoomNumber = classroomToEdit.getRoomNumber(); // lấy ra trước khi thay đổi!

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_RIGHT);

        Button saveButton = new Button("CẬP NHẬT");
        saveButton.getStyleClass().add("create-button");

        Button cancelButton = new Button("HỦY");
        cancelButton.getStyleClass().add("back-button");
        cancelButton.setOnAction(e -> onBackCallback.run());
        header.getChildren().addAll(saveButton, cancelButton);
        mainLayout.setTop(header);

        VBox content = new VBox(25);
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(30);
        inputGrid.setVgap(15);

        // Các field cơ bản
        TextField idField = new TextField();
        idField.setText(classroomToEdit.getId());
        idField.setDisable(true);

        TextField numberField = new TextField(classroomToEdit.getRoomNumber());

        ComboBox<String> buildingCombo = new ComboBox<>();
        buildingCombo.getItems().addAll("Toà HA8", "Toà HA9");
        buildingCombo.setValue(classroomToEdit.buildingProperty().get());

        ComboBox<String> floorCombo = new ComboBox<>();
        floorCombo.getItems().addAll("Tầng 1", "Tầng 2");
        floorCombo.setValue(classroomToEdit.floorProperty().get());

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Phòng Thường", "Phòng Lab");
        typeCombo.setValue(classroomToEdit.getDisplayRoomType());

        // Các combo chọn cảm biến
        ComboBox<Device> tempSensorCombo = createSensorComboBox("TEMPERATURE");
        ComboBox<Device> humiditySensorCombo = createSensorComboBox("HUMIDITY");
        ComboBox<Device> lightSensorCombo = createSensorComboBox("LIGHT");
        ComboBox<Device> co2SensorCombo = createSensorComboBox("CO2");

        // Notes
        TextField notesArea = new TextField();
        notesArea.setMinHeight(50);

        // Gán lại sơ đồ phòng tương ứng
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

        layoutContainer.getChildren().add("Phòng Lab".equals(typeCombo.getValue())
                ? ClassroomLayouts.createLabClassroomLayout(new ArrayList<>())
                : ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>())
        );

        // Thay đổi loại phòng thì thay layout
        typeCombo.setOnAction(e -> updateSensorButtons.run());
        tempSensorCombo.setOnAction(e -> updateSensorButtons.run());
        humiditySensorCombo.setOnAction(e -> updateSensorButtons.run());
        lightSensorCombo.setOnAction(e -> updateSensorButtons.run());
        co2SensorCombo.setOnAction(e -> updateSensorButtons.run());

        // ============ ADD COMPONENTS ============
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
        inputGrid.add(notesArea, 6, 3);

        // ============ END COMPONENTS ============
        Label inputLabel = createHeaderLabel("Thông tin chung");
        Label layoutLabel = createHeaderLabel("Sơ đồ mô phỏng phòng học");
        content.getChildren().addAll(inputLabel, inputGrid, new Separator(), layoutLabel, layoutContainer);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10, 0, 10, 0));
        scrollPane.setStyle("-fx-background-color: transparent;");
        mainLayout.setCenter(scrollPane);

        // ============ LOGIC UPDATE ============
        saveButton.setDisable(true);
        cancelButton.setDisable(true);

        saveButton.setDisable(false);
        cancelButton.setDisable(false);

        saveButton.setOnAction(e -> {
            saveButton.setDisable(true);
            cancelButton.setDisable(true);

            // 1. Cập nhật phòng học
            classroomToEdit.roomNumberProperty().set(numberField.getText());
            classroomToEdit.buildingProperty().set(buildingCombo.getValue());
            classroomToEdit.floorProperty().set(floorCombo.getValue());
            classroomToEdit.displayRoomTypeProperty().set(typeCombo.getValue());

            Roomtype mappedEnum = typeCombo.getValue().equals("Phòng Lab") ? Roomtype.LAB : Roomtype.THUONG;

            classroomToEdit.setRoomType(mappedEnum.name());

            ApiService apiService = new ApiService();
            List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

            // 2. Cập nhật phòng
            updateFutures.add(apiService.updateClassroom(classroomToEdit));

            // 3. Lấy danh sách thiết bị cũ (đang gán vào phòng trước đó)
            List<Device> oldDevices = classroomToEdit.getDevicesInRoom();

            oldDevices.forEach(device -> System.out.println(device.getDeviceId()));

            // 4. Lấy thiết bị mới từ ComboBox (cả thiết bị mới + thiết bị cũ được giữ)
            List<Device> newDevices = new ArrayList<>();
            if (tempSensorCombo.getValue() != null) newDevices.add(tempSensorCombo.getValue());
            if (humiditySensorCombo.getValue() != null) newDevices.add(humiditySensorCombo.getValue());
            if (lightSensorCombo.getValue() != null) newDevices.add(lightSensorCombo.getValue());
            if (co2SensorCombo.getValue() != null) newDevices.add(co2SensorCombo.getValue());

            newDevices.forEach(device -> System.out.println(device.getDeviceId()));
            // 5. Gỡ các thiết bị cũ nếu không còn dùng nữa
            for (Device oldDevice : oldDevices) {
                boolean stillUsed = newDevices.stream()
                        .anyMatch(newDev -> newDev.getDeviceId().equals(oldDevice.getDeviceId()));

                if (!stillUsed) {
                    UpdateDeviceRequest updateReq = new UpdateDeviceRequest(
                            oldDevice.getName(),
                            oldDevice.getType(),
                            "INACTIVE",
                            oldDevice.getDataCycle() != null ? oldDevice.getDataCycle() : 60,
                            oldDevice.getNotes() != null ? oldDevice.getNotes() : "",
                            null);
                    updateFutures.add(apiService.updateDevice(oldDevice.getDeviceId(), updateReq));
                    System.out.println(oldDevice.getDeviceId());
                    System.out.println(oldDevice.getClassroomId());
                }
            }

            // 6. Gán thiết bị mới nếu chưa từng được gán cho phòng này
            for (Device newDevice : newDevices) {
                boolean alreadyUsed = oldDevices.stream()
                        .anyMatch(oldDev -> oldDev.getDeviceId().equals(newDevice.getDeviceId()));

                if (!alreadyUsed) {
                    updateFutures.add(apiService.updateDevice(newDevice.getDeviceId(),
                            new UpdateDeviceRequest(
                                    newDevice.getName(),
                                    newDevice.getType(),
                                    "ACTIVE",
                                    newDevice.getDataCycle() != null ? newDevice.getDataCycle() : 60,
                                    newDevice.getNotes() != null ? newDevice.getNotes() : "",
                                    classroomToEdit.getClassroomId()
                            )));
                }
            }

            // 7. Reload danh sách thiết bị sau khi cập nhật
            CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0]))
                    .thenCompose(v -> apiService.fetchDevices())
                    .thenAccept(freshDevices -> {
                        DataService.setAllDevices(freshDevices);
                        // Cập nhật lại danh sách thiết bị của phòng học này (cực kỳ quan trọng!)
                        List<Device> updatedDevicesInRoom = freshDevices.stream()
                                .filter(d -> classroomToEdit.getRoomNumber().equals(d.getRoom()))
                                .collect(Collectors.toList());
                        classroomToEdit.setDevicesInRoom(updatedDevicesInRoom);
                        Platform.runLater(() -> {
                            new Alert(Alert.AlertType.INFORMATION, "Cập nhật phòng học và thiết bị thành công!").show();
                            onBackCallback.run();
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            new Alert(Alert.AlertType.ERROR, "Có lỗi khi cập nhật: " + ex.getMessage()).show();
                            saveButton.setDisable(false);
                            cancelButton.setDisable(false);
                        });
                        return null;
                    });
        });

        return mainLayout;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("table-title");
        return label;
    }

    private ComboBox<Device> createSensorComboBox(String type) {
        ComboBox<Device> comboBox = new ComboBox<>();
        comboBox.setPromptText("Chọn thiết bị");

        ApiService apiService = new ApiService();
        apiService.fetchDevices().thenAccept(devices -> {
            ObservableList<Device> availableDevices = devices.stream()
                    .filter(d -> type.equals(d.getType()) &&
                            (d.getRoom() == null || d.getRoom().equals(classroomToEdit.getRoomNumber())))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            Platform.runLater(() -> comboBox.setItems(availableDevices));
        });
        comboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Device device) { return device == null ? "" : device.imeiProperty().get(); }
            @Override public Device fromString(String string) { return null; }
        });

        return comboBox;
    }
}