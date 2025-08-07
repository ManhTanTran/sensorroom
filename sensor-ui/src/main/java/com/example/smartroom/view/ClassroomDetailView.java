package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.DeviceData;
import com.example.smartroom.model.DeviceDataMerged;
import com.example.smartroom.service.DataService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ClassroomDetailView {
    private final Classroom classroom;
    private TableView<DeviceDataMerged> measurementTable = new TableView<>();
    private javafx.animation.Timeline autoRefreshTimeline;

    public ClassroomDetailView(Classroom classroom) {
        this.classroom = classroom;
    }

    public Parent getContent() {
        VBox layout = new VBox(10);

        Label measurementLabel = createHeaderLabel("Chất lượng môi trường 3 lần gần nhất");
        CompletableFuture.runAsync(() -> {
            DataService.loadAllDeviceDataFromApi();
            Platform.runLater(() -> {
                measurementTable.getItems().setAll(fetchRecentMergedData());
            });
        });
        //measurementTable.getItems().setAll(initialData);
        setupMeasurementTable(measurementTable);
        startAutoRefresh(); // 👈 Thêm dòng này

        Label layoutLabel = createHeaderLabel("Sơ đồ mô phỏng phòng học");

        List<Device> devicesInThisRoom = classroom.getDevicesInRoom();
        if (devicesInThisRoom == null) {
            devicesInThisRoom = List.of();
        }

        Node classroomLayout;
        if ("Phòng Lab".equals(classroom.displayRoomTypeProperty().get())) {
            classroomLayout = ClassroomLayouts.createLabClassroomLayout(devicesInThisRoom);
        } else {
            classroomLayout = ClassroomLayouts.createRegularClassroomLayout(devicesInThisRoom);
        }

        layout.getChildren().addAll(measurementLabel, measurementTable, layoutLabel, classroomLayout);
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setPadding(new Insets(0, 20, 0, 0));

        return scrollPane;
    }

    public HBox getInfoBox() {
        HBox infoBox = new HBox();
        infoBox.getStyleClass().add("classroom-detail-info-box");

        infoBox.getChildren().addAll(
                createLabelValuePair("Mã phòng học:", classroom.idProperty().get()),
                createLabelValuePair("Số phòng:", classroom.roomNumberProperty().get()),
                createLabelValuePair("Tòa nhà:", classroom.buildingProperty().get()),
                createLabelValuePair("Tầng:", classroom.floorProperty().get()),
                createLabelValuePair("Loại phòng học:", classroom.displayRoomTypeProperty().get())
        );
        return infoBox;
    }

    private void setupMeasurementTable(TableView<DeviceDataMerged> table) {
        TableColumn<DeviceDataMerged, Double> tempCol = new TableColumn<>("Nhiệt độ (°C)");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        TableColumn<DeviceDataMerged, Double> humidityCol = new TableColumn<>("Độ ẩm (%)");
        humidityCol.setCellValueFactory(new PropertyValueFactory<>("humidity"));

        TableColumn<DeviceDataMerged, Double> luxCol = new TableColumn<>("Ánh sáng (lux)");
        luxCol.setCellValueFactory(new PropertyValueFactory<>("lux"));

        TableColumn<DeviceDataMerged, Double> co2Col = new TableColumn<>("CO2 (ppm)");
        co2Col.setCellValueFactory(new PropertyValueFactory<>("co2"));

        TableColumn<DeviceDataMerged, String> timeCol = new TableColumn<>("Thời gian đo gần nhất");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<DeviceDataMerged, String> qualityCol = new TableColumn<>("Chất lượng phòng");
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

        return new HBox(5, titleLabel, valueLabel);
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("table-title");
        return label;
    }

    private List<DeviceDataMerged> fetchRecentMergedData() {
        List<String> deviceIds = classroom.getDevicesInRoom()
                .stream()
                .map(Device::getDeviceId)
                .collect(Collectors.toList());

        List<DeviceData> rawData = DataService.getAllDeviceData()
                .stream()
                .filter(d -> deviceIds.contains(d.getDeviceId()))
                .collect(Collectors.toList());

        return DataService.mergeDeviceData(rawData);
    }

    private void startAutoRefresh() {
        autoRefreshTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(10), event -> {
                    List<DeviceDataMerged> refreshedData = fetchRecentMergedData();
                    measurementTable.getItems().setAll(refreshedData);
                })
        );
        autoRefreshTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }


}
