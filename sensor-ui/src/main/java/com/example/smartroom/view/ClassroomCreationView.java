package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

        inputGrid.add(new Label("Mã phòng học *"), 0, 0); inputGrid.add(idField, 1, 0);
        inputGrid.add(new Label("Số phòng *"), 0, 1); inputGrid.add(numberField, 1, 1);
        inputGrid.add(new Label("Tòa nhà *"), 0, 2); inputGrid.add(buildingCombo, 1, 2);
        inputGrid.add(new Label("Tầng *"), 0, 3); inputGrid.add(floorCombo, 1, 3);
        inputGrid.add(new Label("Loại phòng học *"), 2, 0); inputGrid.add(typeCombo, 3, 0);

        inputGrid.add(new Label("Ghi chú"), 2, 1);
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(5);
        GridPane.setColumnSpan(notesArea, 2);
        GridPane.setRowSpan(notesArea, 3);
        inputGrid.add(notesArea, 3, 1);

        StackPane layoutContainer = new StackPane();
        layoutContainer.getChildren().add(ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>()));

        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            layoutContainer.getChildren().clear();
            if ("Phòng Lab".equals(newVal)) {
                layoutContainer.getChildren().add(ClassroomLayouts.createLabClassroomLayout(new ArrayList<>()));
            } else {
                layoutContainer.getChildren().add(ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>()));
            }
        });

        content.getChildren().addAll(new Label("Thông tin chung"), inputGrid, new Separator(), new Label("Sơ đồ mô phỏng phòng học"), layoutContainer);
        mainLayout.setCenter(content);
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

            new Alert(Alert.AlertType.INFORMATION, "Đã tạo phòng học mới thành công!").show();

            // Quay trở lại danh sách
            onBackCallback.run();
        });

        return mainLayout;
    }
}