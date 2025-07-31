package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.ClassroomMeasurement;
import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ClassroomDetailView {
    private final Classroom classroom;

    public ClassroomDetailView(Classroom classroom) {
        this.classroom = classroom;
    }

    /**
     * Phương thức này giờ đây trả về một VBox chứa các thành phần CHÍNH của view chi tiết.
     */
    public Parent getContent() {
        VBox layout = new VBox(10);

        Label measurementLabel = createHeaderLabel("Thông số đo 3 lần gần nhất của phòng");
        TableView<ClassroomMeasurement> measurementTable = new TableView<>(DataService.getRoomMeasurements());
        setupMeasurementTable(measurementTable);

        Label layoutLabel = createHeaderLabel("Sơ đồ mô phỏng phòng học");

        List<Device> devicesInThisRoom = DataService.getDevicesByRoomId(classroom.getId());


        Node classroomLayout;
        // Kiểm tra giá trị của thuộc tính roomType, không phải một chuỗi cố định
        if ("Phòng Lab".equals(classroom.roomTypeProperty().get())) {
            classroomLayout = ClassroomLayouts.createLabClassroomLayout(devicesInThisRoom);
        } else {
            classroomLayout = ClassroomLayouts.createRegularClassroomLayout(devicesInThisRoom);
        }

        layout.getChildren().addAll(measurementLabel, measurementTable, layoutLabel, classroomLayout);
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true); // ← tự kéo giãn theo chiều ngang
        scrollPane.setStyle("-fx-background-color:transparent;"); // ← không viền khó chịu
        scrollPane.setPadding(new Insets(0, 20, 0, 0)); // ← tuỳ chỉnh nếu muốn thoáng bên phải

        return scrollPane;
    }

    /**
     * Trả về thanh thông tin (infoBox) để view cha có thể sử dụng.
     */
    public HBox getInfoBox() {
        HBox infoBox = new HBox();
        infoBox.getStyleClass().add("classroom-detail-info-box");

        infoBox.getChildren().addAll(
                createLabelValuePair("Mã phòng học:", classroom.idProperty().get()),
                createLabelValuePair("Số phòng:", classroom.roomNumberProperty().get()),
                createLabelValuePair("Tòa nhà:", classroom.buildingProperty().get()),
                createLabelValuePair("Tầng:", classroom.floorProperty().get()),
                createLabelValuePair("Loại phòng học:", classroom.roomTypeProperty().get())
        );
        return infoBox;
    }

    private void setupMeasurementTable(TableView<ClassroomMeasurement> table) {
        TableColumn<ClassroomMeasurement, String> tempCol = new TableColumn<>("Nhiệt độ (°C)");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        TableColumn<ClassroomMeasurement, String> humidityCol = new TableColumn<>("Độ ẩm (%)");
        humidityCol.setCellValueFactory(new PropertyValueFactory<>("humidity"));

        TableColumn<ClassroomMeasurement, String> luxCol = new TableColumn<>("Ánh sáng (lux)");
        luxCol.setCellValueFactory(new PropertyValueFactory<>("lux"));

        TableColumn<ClassroomMeasurement, String> co2Col = new TableColumn<>("CO2 (ppm)");
        co2Col.setCellValueFactory(new PropertyValueFactory<>("co2"));

        TableColumn<ClassroomMeasurement, String> timeCol = new TableColumn<>("Thời gian đo gần nhất");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<ClassroomMeasurement, String> qualityCol = new TableColumn<>("Chất lượng phòng");
        qualityCol.setCellValueFactory(new PropertyValueFactory<>("quality"));
        qualityCol.setCellFactory(ViewHelper.createStyledCellFactory("status-"));

        table.getColumns().setAll(tempCol, humidityCol, luxCol, co2Col, timeCol, qualityCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(350);
    }

    private Node createLabelValuePair(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-title-label");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("info-value-label");

        HBox pair = new HBox(5, titleLabel, valueLabel);
        return pair;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("table-title");
        return label;
    }
}