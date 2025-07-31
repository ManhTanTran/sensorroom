package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;

public class ClassroomEditView {
    private final Runnable onBackCallback;
    private final Classroom classroomToEdit;

    public ClassroomEditView(Runnable onBackCallback, Classroom classroomToEdit) {
        this.onBackCallback = onBackCallback;
        this.classroomToEdit = classroomToEdit;
    }

    public Parent getView() {
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

        VBox content = new VBox(20);
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

        idField.setText(classroomToEdit.getId());
        idField.setDisable(true);
        numberField.setText(classroomToEdit.getRoomNumber());
        buildingCombo.setValue(classroomToEdit.getBuilding());
        floorCombo.setValue(classroomToEdit.floorProperty().get());
        typeCombo.setValue(classroomToEdit.roomTypeProperty().get());

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

        inputGrid.add(new Label("Ghi chú"), 2, 1);
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(5);
        GridPane.setColumnSpan(notesArea, 2);
        GridPane.setRowSpan(notesArea, 3);
        inputGrid.add(notesArea, 3, 1);

        StackPane layoutContainer = new StackPane();
        if ("Phòng Lab".equals(typeCombo.getValue())) {
            layoutContainer.getChildren().add(ClassroomLayouts.createLabClassroomLayout(new ArrayList<>()));
        } else {
            layoutContainer.getChildren().add(ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>()));
        }

        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            layoutContainer.getChildren().clear();
            if ("Phòng Lab".equals(newVal)) {
                layoutContainer.getChildren().add(ClassroomLayouts.createLabClassroomLayout(new ArrayList<>()));
            } else {
                layoutContainer.getChildren().add(ClassroomLayouts.createRegularClassroomLayout(new ArrayList<>()));
            }
        });

        Label inputLabel = createHeaderLabel("Thông tin chung");
        Label layoutLabel = createHeaderLabel("Sơ đồ mô phỏng phòng học");
        content.getChildren().addAll(inputLabel, inputGrid, new Separator(), layoutLabel, layoutContainer);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10, 0, 10, 0));
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);

        saveButton.setOnAction(e -> {
            classroomToEdit.roomNumberProperty().set(numberField.getText());
            classroomToEdit.buildingProperty().set(buildingCombo.getValue());
            classroomToEdit.floorProperty().set(floorCombo.getValue());
            classroomToEdit.roomTypeProperty().set(typeCombo.getValue());

            new Alert(Alert.AlertType.INFORMATION, "Đã cập nhật thông tin phòng học thành công!").show();
            onBackCallback.run();
        });

        return mainLayout;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("table-title");
        return label;
    }
}