package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClassroomCreationView {

    // Callback để báo cho view cha biết khi nào cần quay lại
    private final Runnable onBackCallback;

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
        buildingCombo.getItems().addAll("Tòa A", "Tòa B", "Tòa C");
        ComboBox<String> floorCombo = new ComboBox<>();
        floorCombo.getItems().addAll("Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4", "Tầng 5");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Phòng học", "Phòng Lab");
        typeCombo.getSelectionModel().selectFirst();
        ComboBox<Device> tempSensorCombo = createSensorComboBox("Cảm biến nhiệt độ");
        ComboBox<Device> humiditySensorCombo = createSensorComboBox("Cảm biến độ ẩm");
        ComboBox<Device> lightSensorCombo = createSensorComboBox("Cảm biến ánh sáng");
        ComboBox<Device> co2SensorCombo = createSensorComboBox("Cảm biến CO2");

        inputGrid.add(new Label("Mã phòng học *"), 0, 0);
        inputGrid.add(idField, 1, 0);
        inputGrid.add(new Label("Số phòng *"), 0, 1);
        inputGrid.add(numberField, 1, 1);
        inputGrid.add(new Label("Tòa nhà *"), 0, 2);
        inputGrid.add(buildingCombo, 1, 2);
        inputGrid.add(new Label("Tầng *"), 0, 3);
        inputGrid.add(floorCombo, 1, 3);
        inputGrid.add(new Label("Loại phòng học *"), 2, 0);
        inputGrid.add(typeCombo, 3, 0);
        inputGrid.add(new Label("Nhiệt độ"), 4, 0);
        inputGrid.add(tempSensorCombo, 5, 0);
        inputGrid.add(new Label("Độ ẩm"), 6, 0);
        inputGrid.add(humiditySensorCombo, 7, 0);
        inputGrid.add(new Label("Ánh sáng"), 4, 1);
        inputGrid.add(lightSensorCombo, 5, 1);
        inputGrid.add(new Label("CO2"), 6, 1);
        inputGrid.add(co2SensorCombo, 7, 1);


        inputGrid.add(new Label("Ghi chú"), 2, 2);
        TextField notesArea = new TextField();
        notesArea.setMinHeight(50);
        inputGrid.add(notesArea, 3, 2);

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
            String id = idField.getText();
            String number = numberField.getText();
            String building = buildingCombo.getValue();
            String floor = floorCombo.getValue();
            String roomType = typeCombo.getValue();

            if (id.isEmpty() || number.isEmpty() || building == null || floor == null || roomType == null) {
                new Alert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ các trường bắt buộc (*).").show();
                return;
            }

            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // TẠO ĐỐI TƯỢNG MỚI VỚI HÀM TẠO ĐẦY ĐỦ
            Classroom newClassroom = new Classroom(
                    id, number, building, floor, roomType,
                    currentDate, "Hoạt động",
                    25, 50, 400, 800,
                    DataService.getAllDevices() // Truyền vào master list
            );

            // THÊM VÀO DANH SÁCH GỐC
            DataService.addClassroom(newClassroom);
            /*
            if (tempSensorCombo.getValue() != null) tempSensorCombo.getValue().roomProperty().set(id);
            if (humiditySensorCombo.getValue() != null) humiditySensorCombo.getValue().roomProperty().set(id);
            if (lightSensorCombo.getValue() != null) lightSensorCombo.getValue().roomProperty().set(id);
            if (co2SensorCombo.getValue() != null) co2SensorCombo.getValue().roomProperty().set(id);
            */
            updateDevice(tempSensorCombo.getValue(), id);
            updateDevice(humiditySensorCombo.getValue(), id);
            updateDevice(lightSensorCombo.getValue(), id);
            updateDevice(co2SensorCombo.getValue(), id);



            new Alert(Alert.AlertType.INFORMATION, "Đã tạo phòng học mới thành công!").show();

            // Quay trở lại danh sách
            onBackCallback.run();
        });

        return mainLayout;
    }

    private ComboBox<Device> createSensorComboBox(String type) {
        ComboBox<Device> comboBox = new ComboBox<>();
        comboBox.setPromptText("Chọn thiết bị");
        ObservableList<Device> availableDevices = DataService.getAllDevices().filtered(
                d -> d.getRoom() == null && type.equals(d.getType())
        );
        comboBox.setItems(availableDevices);

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