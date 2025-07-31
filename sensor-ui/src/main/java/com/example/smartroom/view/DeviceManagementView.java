package com.example.smartroom.view;

import com.example.smartroom.model.Device;
import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;


public class DeviceManagementView {

    private final int ROWS_PER_PAGE = 10;
    private TableView<Device> deviceTable;
    private FilteredList<Device> filteredData;

    public Node getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10, 0, 0, 0));

        User currentUser = UserSession.getInstance().getUser();
        ObservableList<Device> deviceData = (currentUser.role() == Role.ADMIN)
                ? DataService.getAllDevices()
                : DataService.getDevicesForKtv(currentUser.managedRooms());

        filteredData = new FilteredList<>(deviceData, p -> true);

        HBox topBox = createTopBox(deviceData, filteredData);

        deviceTable = new TableView<>();
        VBox.setVgrow(deviceTable, Priority.ALWAYS);
        createDeviceTableColumns(deviceTable, deviceData);

        Pagination pagination = new Pagination();

        filteredData.addListener((ListChangeListener.Change<? extends Device> c) ->
                updatePaginationCount(pagination, filteredData)
        );
        updatePaginationCount(pagination, filteredData);

        pagination.setPageFactory(this::createPage);

        layout.getChildren().addAll(topBox, deviceTable, pagination);
        return layout;
    }

    private void updatePaginationCount(Pagination pagination, FilteredList<Device> data) {
        int pageCount = (int) Math.ceil((double) data.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(1, pageCount));
        if (pagination.getCurrentPageIndex() >= pageCount && pageCount > 0) {
            pagination.setCurrentPageIndex(pageCount - 1);
        }
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        if (fromIndex <= toIndex) {
            deviceTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        }
        return new VBox();
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
        roomFilter.getItems().addAll(sourceData.stream().map(Device::getRoom).filter(java.util.Objects::nonNull).distinct().sorted().collect(Collectors.toList()));
        roomFilter.setValue("Tất cả");

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(14);
        Button searchButton = new Button("", searchIcon);
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
                boolean roomMatch = roomValue == null || roomValue.equals("Tất cả") || (device.getRoom() != null && device.getRoom().equals(roomValue));
                return imeiMatch && typeMatch && roomMatch;
            });
        });
        return filterBox;
    }

    private void createDeviceTableColumns(TableView<Device> table, ObservableList<Device> sourceList) {
        TableColumn<Device, String> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Device, String> imeiCol = new TableColumn<>("IMEI / Serial");
        imeiCol.setCellValueFactory(new PropertyValueFactory<>("imei"));

        TableColumn<Device, String> typeCol = new TableColumn<>("Loại cảm biến");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Device, String> roomCol = new TableColumn<>("Phòng học");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<Device, String> valueCol = new TableColumn<>("Thông số gần nhất");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<Device, String> lastActiveCol = new TableColumn<>("Hoạt động cuối");
        lastActiveCol.setCellValueFactory(new PropertyValueFactory<>("lastActive"));

        TableColumn<Device, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ViewHelper.createStyledCellFactory("status-"));

        TableColumn<Device, Void> actionCol = new TableColumn<>("Thao tác");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button();

            {
                FontIcon trashIcon = new FontIcon(FontAwesomeSolid.TRASH);
                trashIcon.setIconSize(16);
                trashIcon.setIconColor(Color.web("#DC2626"));

                deleteBtn.setGraphic(trashIcon);
                deleteBtn.getStyleClass().add("icon-button");

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
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().setAll(
                sttCol, imeiCol, typeCol, roomCol, valueCol,
                lastActiveCol, statusCol, actionCol
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.getStyleClass().add("table-cell");
    }

}