package com.example.smartroom.view;

import com.example.smartroom.model.*;
import com.example.smartroom.service.ApiService;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DeviceManagementView {

    private final int ROWS_PER_PAGE = 10;
    private TableView<Device> deviceTable;
    private FilteredList<Device> filteredData;

    public Node getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10, 0, 0, 0));

        User currentUser = UserSession.getInstance().getUser();
        boolean isAdmin = (currentUser.role() == Role.ADMIN);
        ObservableList<Device> deviceData = DataService.getAllDevices();

        ApiService apiService = new ApiService();
        apiService.fetchDevices().thenAccept(devices -> {
            apiService.fetchAllDeviceData().thenAccept(deviceDataList -> {
                for (Device device : devices) {
                    DeviceData latestData = null;

                    for (DeviceData data : deviceDataList) {
                        if (data.getDeviceId().equals(device.getDeviceId())) {
                            if (latestData == null || data.getCreatedAt().compareTo(latestData.getCreatedAt()) > 0) {
                                latestData = data;
                            }
                        }
                    }

                    if (latestData != null) {
                        String value = switch (device.getType()) {
                            case "TEMPERATURE" -> latestData.getTemperature() != null ? latestData.getTemperature() + " °C" : "-";
                            case "HUMIDITY"   -> latestData.getHumidity() != null ? latestData.getHumidity() + " %" : "-";
                            case "LIGHT"      -> latestData.getLight() != null ? latestData.getLight() + " lux" : "-";
                            case "CO2"        -> latestData.getCo2() != null ? String.format("%.0f ppm", latestData.getCo2()) : "-";
                            default -> "-";
                        };
                        device.setValue(value);

                        try {
                            LocalDateTime dateTime = LocalDateTime.parse(latestData.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                            device.setLastActive(formattedTime);
                        } catch (Exception e) {
                            device.setLastActive(latestData.getCreatedAt());
                        }
                    }
                }


                if (!isAdmin) {
                    List<Classroom> allowedRooms = currentUser.managedRooms();
                    Set<String> managedRoomNumbers = allowedRooms.stream()
                            .map(Classroom::getRoomNumber)
                            .collect(Collectors.toSet());
                    System.out.println("🧠 KTV được phép xem các phòng: " + allowedRooms);
                    devices.removeIf(d -> !managedRoomNumbers.contains(d.getRoom()));
                }

                javafx.application.Platform.runLater(() -> {
                    deviceData.setAll(devices);
                    filteredData = new FilteredList<>(deviceData, p -> true);

                    HBox topBox = createTopBox(deviceData, filteredData);
                    deviceTable = new TableView<>();
                    VBox.setVgrow(deviceTable, Priority.ALWAYS);
                    createDeviceTableColumns(deviceTable, deviceData);
                    deviceTable.setRowFactory(tv -> new TableRow<>() {
                        @Override
                        protected void updateItem(Device device, boolean empty) {
                            super.updateItem(device, empty);

                            if (device == null || empty) {
                                setStyle("");
                                return;
                            }

                            // Check ngưỡng và set màu nếu vượt
                            String type = device.getType();
                            String valueStr = device.valueProperty().get(); // ví dụ "28 °C", "70 %", "410 ppm", "300 lux"
                            try {
                                double number = Double.parseDouble(valueStr.replaceAll("[^\\d.]", ""));

                                boolean isDanger = switch (type) {
                                    case "TEMPERATURE" -> number < 18 || number > 30;
                                    case "HUMIDITY"    -> number < 35 || number > 70;
                                    case "LIGHT"       -> number < 200 || number > 1500;
                                    case "CO2"         -> number > 1500;
                                    default -> false;
                                };

                                if (isDanger) {
                                    setStyle("-fx-background-color: #FCA5A5;"); // light red
                                } else {
                                    setStyle("");
                                }
                            } catch (Exception e) {
                                setStyle(""); // nếu parse lỗi thì bỏ qua
                            }
                        }
                    });


                    Pagination pagination = new Pagination();
                    filteredData.addListener((ListChangeListener.Change<? extends Device> c) ->
                            updatePaginationCount(pagination, filteredData));
                    updatePaginationCount(pagination, filteredData);
                    pagination.setPageFactory(this::createPage);

                    layout.getChildren().setAll(topBox, deviceTable, pagination);

                    startAutoUpdate(deviceData, isAdmin);

                });
            });
        });

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

        Label searchLabel = new Label("Tìm kiếm:");

        TextField imeiFilter = new TextField();
        imeiFilter.setPromptText("Mã thiết bị");

        TextField roomFilter = new TextField();
        roomFilter.setPromptText("Số phòng");

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.setPromptText("Loại cảm biến");
        typeFilter.getItems().add("Tất cả");
        typeFilter.getItems().addAll(
                sourceData.stream()
                        .map(Device::getType)
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .toList()
        );
        typeFilter.setValue("Tất cả");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.setPromptText("Trạng thái");
        statusFilter.getItems().addAll("Tất cả", "ACTIVE", "INACTIVE");
        statusFilter.setValue("Tất cả");

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(14);
        Button searchButton = new Button("", searchIcon);
        searchButton.getStyleClass().add("search-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(
                searchLabel, imeiFilter, roomFilter,
                new Label("Loại cảm biến: "), typeFilter,
                new Label ("Trạng thái: "), statusFilter, searchButton, spacer
        );

        searchButton.setOnAction(e -> {
            String imeiVal = imeiFilter.getText().trim().toLowerCase();
            String roomVal = roomFilter.getText().trim().toLowerCase();
            String typeVal = typeFilter.getValue();
            String statusVal = statusFilter.getValue();

            filteredData.setPredicate(device -> {
                boolean imeiMatch = imeiVal.isEmpty() || device.imeiProperty().get().toLowerCase().contains(imeiVal);
                boolean roomMatch = roomVal.isEmpty() || (device.getRoom() != null && device.getRoom().toLowerCase().contains(roomVal));
                boolean typeMatch = typeVal == null || typeVal.equals("Tất cả") || device.getType().equalsIgnoreCase(typeVal);
                boolean statusMatch = statusVal == null || statusVal.equals("Tất cả") || device.getStatus().equalsIgnoreCase(statusVal);

                return imeiMatch && roomMatch && typeMatch && statusMatch;
            });
        });

        return filterBox;
    }



    private void createDeviceTableColumns(TableView<Device> table, ObservableList<Device> sourceList) {
        TableColumn<Device, String> sttCol = new TableColumn<>("STT");
        Label sttLabel = new Label("STT");
        //sttCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sttCol.setCellFactory(col -> {
            TableCell<Device, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null) {
                        setText(null);
                    } else {
                        setText(Integer.toString(getIndex() + 1));
                    }
                    //setAlignment(Pos.CENTER_RIGHT);
                }
            };
            return cell;
        });

        TableColumn<Device, String> imeiCol = new TableColumn<>("Mã thiết bị");
        imeiCol.setCellValueFactory(new PropertyValueFactory<>("imei"));

        TableColumn<Device, String> typeCol = new TableColumn<>("Loại cảm biến");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedType()));

        TableColumn<Device, String> roomCol = new TableColumn<>("Phòng học");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<Device, String> valueCol = new TableColumn<>("Thông số gần nhất");
        //valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setCellValueFactory(cellData -> cellData.getValue().valueProperty());


        TableColumn<Device, String> lastActiveCol = new TableColumn<>("Hoạt động cuối");
        lastActiveCol.setCellValueFactory(cellData -> cellData.getValue().lastActiveProperty());

        TableColumn<Device, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedStatus()));
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
                        ApiService apiService = new ApiService();
                        apiService.deleteDevice(device.getId()).thenRun(() -> {
                            javafx.application.Platform.runLater(() -> {
                                sourceList.remove(device);
                                Alert success = new Alert(Alert.AlertType.INFORMATION);
                                success.setTitle("Xoá thành công");
                                success.setHeaderText(null);
                                success.setContentText("Thiết bị đã được xoá.");
                                success.showAndWait();
                            });
                        }).exceptionally(ex -> {
                            javafx.application.Platform.runLater(() -> {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Lỗi khi xoá thiết bị");
                                error.setHeaderText(null);
                                error.setContentText("Không thể xoá thiết bị: " + ex.getMessage());
                                error.showAndWait();
                            });
                            return null;
                        });
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

    private void startAutoUpdate(ObservableList<Device> deviceData, boolean isAdmin) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), event -> {
                    ApiService apiService = new ApiService();
                    apiService.fetchAllDeviceData().thenAccept(deviceDataList -> {
                        Platform.runLater(() -> {
                            for (Device device : deviceData) {
                                DeviceData latestData = null;

                                for (DeviceData data : deviceDataList) {
                                    if (data.getDeviceId().equals(device.getDeviceId())) {
                                        if (latestData == null || data.getCreatedAt().compareTo(latestData.getCreatedAt()) > 0) {
                                            latestData = data;
                                        }
                                    }
                                }

                                if (latestData != null) {
                                    String value = switch (device.getType()) {
                                        case "TEMPERATURE" -> latestData.getTemperature() != null ? latestData.getTemperature() + " °C" : "-";
                                        case "HUMIDITY"   -> latestData.getHumidity() != null ? latestData.getHumidity() + " %" : "-";
                                        case "LIGHT"      -> latestData.getLight() != null ? latestData.getLight() + " lux" : "-";
                                        case "CO2"        -> latestData.getCo2() != null ? String.format("%.0f ppm", latestData.getCo2()) : "-";
                                        default -> "-";
                                    };

                                    device.setValue(value);

                                    try {
                                        LocalDateTime dateTime = LocalDateTime.parse(latestData.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                        String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                                        device.setLastActive(formattedTime);
                                    } catch (Exception e) {
                                        device.setLastActive(latestData.getCreatedAt());
                                    }
                                }
                            }
                            deviceTable.refresh(); // cập nhật UI
                        });
                    });
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}