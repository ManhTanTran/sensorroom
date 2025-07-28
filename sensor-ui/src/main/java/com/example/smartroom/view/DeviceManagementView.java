package com.example.smartroom.view;

import com.example.smartroom.model.Device;
import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.stream.Collectors;

public class DeviceManagementView {

    public Node getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10, 0, 0, 0));

        User currentUser = UserSession.getInstance().getUser();

        ObservableList<Device> deviceData = (currentUser.role() == Role.ADMIN)
                ? DataService.getAllDevices()
                : DataService.getDevicesForKtv(currentUser.managedRooms());

        FilteredList<Device> filteredData = new FilteredList<>(deviceData, p -> true);

        HBox topBox = createTopBox(deviceData, filteredData);
        TableView<Device> deviceTable = createDeviceTable(filteredData, deviceData);
        VBox.setVgrow(deviceTable, Priority.ALWAYS);
        Pagination pagination = new Pagination(10, 0);

        layout.getChildren().addAll(topBox, deviceTable, pagination);
        return layout;
    }

    private HBox createTopBox(ObservableList<Device> sourceData, FilteredList<Device> filteredData) {
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getStyleClass().add("filter-box");

        TextField imeiFilter = new TextField();
        imeiFilter.setPromptText("IMEI / Serial");

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.setPromptText("Loại cảm biến");
        typeFilter.getItems().add("Tất cả");
        typeFilter.getItems().addAll(sourceData.stream().map(Device::getType).distinct().sorted().collect(Collectors.toList()));
        typeFilter.setValue("Tất cả");

        ComboBox<String> roomFilter = new ComboBox<>();
        roomFilter.setPromptText("Phòng học");
        roomFilter.getItems().add("Tất cả");
        roomFilter.getItems().addAll(sourceData.stream().map(Device::getRoom).distinct().sorted().collect(Collectors.toList()));
        roomFilter.setValue("Tất cả");

        Button searchButton = new Button("🔍 Tìm kiếm");
        searchButton.getStyleClass().add("search-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(new Label("Tìm kiếm:"), imeiFilter, typeFilter, roomFilter, searchButton, spacer);

        searchButton.setOnAction(e -> {
            String imeiValue = imeiFilter.getText().toLowerCase().trim();
            String typeValue = typeFilter.getValue();
            String roomValue = roomFilter.getValue();

            filteredData.setPredicate(device -> {
                boolean imeiMatch = imeiValue.isEmpty() || device.imeiProperty().get().toLowerCase().contains(imeiValue);
                boolean typeMatch = typeValue == null || typeValue.equals("Tất cả") || device.getType().equals(typeValue);
                boolean roomMatch = roomValue == null || roomValue.equals("Tất cả") || device.getRoom().equals(roomValue);
                return imeiMatch && typeMatch && roomMatch;
            });

            if (filteredData.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Không tìm thấy thiết bị nào phù hợp.").show();
            }
        });
        return filterBox;
    }

    private TableView<Device> createDeviceTable(FilteredList<Device> filteredData, ObservableList<Device> sourceList) {
        TableView<Device> table = new TableView<>(filteredData);

        TableColumn<Device, String> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Device, String> imeiCol = new TableColumn<>("IMEI / Serial");
        imeiCol.setCellValueFactory(new PropertyValueFactory<>("imei"));

        TableColumn<Device, String> typeCol = new TableColumn<>("Loại cảm biến");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Device, String> roomCol = new TableColumn<>("Phòng học");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        // CỘT DỮ LIỆU ĐÃ ĐƯỢC THÊM LẠI
        TableColumn<Device, String> valueCol = new TableColumn<>("Thông số gần nhất");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<Device, String> lastActiveCol = new TableColumn<>("Hoạt động cuối");
        lastActiveCol.setCellValueFactory(new PropertyValueFactory<>("lastActive"));

        TableColumn<Device, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ViewHelper.createStyledCellFactory("status-"));

        // CHỨC NĂNG XÓA ĐÃ ĐƯỢC KHÔI PHỤC
        TableColumn<Device, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑️");
            {
                deleteBtn.getStyleClass().add("action-button-delete");
                deleteBtn.setOnAction(event -> {
                    Device device = getTableView().getItems().get(getIndex());

                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Xác nhận xóa");
                    confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa thiết bị này không?");
                    confirmAlert.setContentText("Thiết bị: " + device.imeiProperty().get());

                    Optional<ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        sourceList.remove(device);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().setAll(sttCol, imeiCol, typeCol, roomCol, valueCol, lastActiveCol, statusCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }
}