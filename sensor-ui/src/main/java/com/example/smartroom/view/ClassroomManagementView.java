package com.example.smartroom.view;

import com.example.smartroom.model.*;
import com.example.smartroom.service.ApiService;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import com.example.smartroom.util.ResourceLoader;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ClassroomManagementView {

    private StackPane mainContentPane;
    private Node classroomListView;
    private ObservableList<Classroom> classroomData = DataService.getAllClassrooms();

    public Node getView() {
        this.mainContentPane = new StackPane();
        this.classroomListView = createClassroomListView();
        this.mainContentPane.getChildren().add(this.classroomListView);
        return this.mainContentPane;
    }


    private Node createClassroomListView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("management-view-container");

        User currentUser = UserSession.getInstance().getUser();
        //ObservableList<Classroom> classroomData = FXCollections.observableArrayList();

        DataService.fetchClassroomsWithDevices().thenAccept(allClassrooms -> {
            ObservableList<Classroom> filteredClassrooms;
            if (currentUser.role() == Role.ADMIN) {
                filteredClassrooms = allClassrooms;
            } else {
                Set<String> managedRoomNumbers = currentUser.managedRooms().stream()
                        .map(Classroom::getRoomNumber)
                        .collect(Collectors.toSet());
                filteredClassrooms = allClassrooms.stream()
                        .filter(c -> managedRoomNumbers.contains(c.getRoomNumber()))
                        .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
            }

            for (int i = 0; i < filteredClassrooms.size(); i++) {
                Classroom c = filteredClassrooms.get(i);
                c.postProcess();
                if (c.formattedCreatedAtProperty().get() == null || c.formattedCreatedAtProperty().get().isEmpty()) {
                    String fake = LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    c.formattedCreatedAtProperty().set(fake);
                }
            }


            javafx.application.Platform.runLater(() -> {
                classroomData.setAll(filteredClassrooms);
            });
        });

        FilteredList<Classroom> filteredData = new FilteredList<>(classroomData, p -> true);
        HBox topBox = createFilterBox(filteredData);

        VBox tableContainer = new VBox();
        tableContainer.getStyleClass().add("table-pane");
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        TableView<Classroom> classroomTable = createClassroomTable(filteredData);
        tableContainer.getChildren().add(classroomTable);

        layout.getChildren().addAll(topBox, tableContainer);
        return layout;
    }

    private HBox createFilterBox(FilteredList<Classroom> filteredData) {
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getStyleClass().add("filter-box");

        filterBox.getChildren().add(new Label("Tìm kiếm:"));

        TextField idFilter = new TextField();
        idFilter.setPromptText("Mã phòng");

        TextField roomNumberFilter = new TextField();
        roomNumberFilter.setPromptText("Số phòng");

        ComboBox<String> buildingFilter = new ComboBox<>();
        buildingFilter.setPromptText("Tòa nhà");
        buildingFilter.getItems().addAll("Tất cả", "Tòa HA8", "Tòa HA9");
        buildingFilter.setValue("Tất cả");

        ComboBox<String> floorFilter = new ComboBox<>();
        floorFilter.setPromptText("Tầng");
        floorFilter.getItems().addAll("Tất cả", "1", "2");
        floorFilter.setValue("Tất cả");
        ComboBox<String> roomTypeFilter = new ComboBox<>();
        roomTypeFilter.setPromptText("Loại phòng");
        roomTypeFilter.getItems().addAll("Tất cả", "Phòng Lab", "Phòng Thường");
        roomTypeFilter.setValue("Tất cả");

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(14);
        Button searchButton = new Button("", searchIcon);

        FontIcon plusIcon = new FontIcon(FontAwesomeSolid.PLUS);
        plusIcon.setIconSize(14);
        plusIcon.setIconColor(Color.WHITE);
        Button createButton = new Button(" Tạo mới", plusIcon);
        createButton.setContentDisplay(ContentDisplay.LEFT);
        createButton.getStyleClass().add("create-button");
        User currentUser = UserSession.getInstance().getUser();
        createButton.setDisable(currentUser.role() != Role.ADMIN);

        createButton.setOnAction(e -> {
            refreshView();
            showCreateNewClassroomView();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(
                idFilter, roomNumberFilter,
                new Label ("Toà nhà: "), buildingFilter,
                new Label ("Tầng: "), floorFilter,
                new Label ("Loại phòng: "), roomTypeFilter, searchButton, spacer, createButton
        );

        searchButton.setOnAction(e -> {
            String idVal = idFilter.getText().trim().toLowerCase();
            String roomNumVal = roomNumberFilter.getText().trim().toLowerCase();
            String buildingVal = buildingFilter.getValue();
            String floorVal = floorFilter.getValue();
            String roomTypeVal = roomTypeFilter.getValue();

            filteredData.setPredicate(classroom -> {
                boolean idMatch = idVal.isEmpty() || classroom.idProperty().get().toLowerCase().contains(idVal);
                boolean roomNumMatch = roomNumVal.isEmpty() || classroom.roomNumberProperty().get().toLowerCase().contains(roomNumVal);
                boolean buildingMatch = buildingVal.equals("Tất cả") || classroom.buildingProperty().get().equalsIgnoreCase(buildingVal);
                boolean floorMatch = floorVal.equals("Tất cả") || classroom.floorProperty().get().equalsIgnoreCase(floorVal);
                boolean roomTypeMatch = roomTypeVal.equals("Tất cả") || classroom.displayRoomTypeProperty().get().equalsIgnoreCase(roomTypeVal);

                return idMatch && roomNumMatch && buildingMatch && floorMatch && roomTypeMatch;
            });
        });

        return filterBox;
    }


    private TableView<Classroom> createClassroomTable(FilteredList<Classroom> data) {
        TableView<Classroom> table = new TableView<>(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- STT column ---
        TableColumn<Classroom, String> sttCol = new TableColumn<>("STT");
        Label sttLabel = new Label("STT");
        sttLabel.setMaxWidth(Double.MAX_VALUE);

        //sttCol.setGraphic(sttLabel);
        sttCol.setCellFactory(col -> {
            TableCell<Classroom, String> cell = new TableCell<>() {
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

        // --- Các cột còn lại ---
        TableColumn<Classroom, String> idCol = createCenteredColumn("Mã phòng", "id");
        TableColumn<Classroom, String> numberCol = createCenteredColumn("Số phòng", "roomNumber");
        TableColumn<Classroom, String> buildingCol = createCenteredColumn("Tòa nhà", "building");
        TableColumn<Classroom, String> floorCol = createCenteredColumn("Tầng", "floor");
        TableColumn<Classroom, String> typeCol = createCenteredColumn("Loại phòng", "displayRoomType");
        TableColumn<Classroom, Integer> deviceCountCol = createCenteredColumn("Số Thiết Bị", "deviceCount");
        TableColumn<Classroom, String> createdDateCol = new TableColumn<>("Ngày tạo");
        createdDateCol.setCellValueFactory(cellData ->
                cellData.getValue().formattedCreatedAtProperty());
        createdDateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                System.out.println("Cell date shown: " + item);
                setText(empty || item == null ? null : item);
            }
        });
        TableColumn<Classroom, String> statusCol = createStatusToggleColumn("Trạng thái");
        TableColumn<Classroom, Void> actionCol = createActionColumn();

        table.getColumns().setAll(
                sttCol, idCol, numberCol, buildingCol, floorCol,
                typeCol, deviceCountCol, createdDateCol, statusCol, actionCol
        );

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Classroom classroom, boolean empty) {
                super.updateItem(classroom, empty);

                if (empty || classroom == null) {
                    setStyle("");
                    return;
                }

                //var devices = classroom.getDevicesInRoom(); // Sử dụng đúng hàm bạn có
                List<Device> devices = DataService.getAllDevices().stream()
                        .filter(d -> classroom.getRoomNumber().equals(d.getRoom()))
                        .collect(Collectors.toList());

                if (devices == null || devices.isEmpty()) {
                    setStyle("");
                    return;
                }

                boolean hasWarning = devices.stream().anyMatch(device -> {
                    String type = device.getType();
                    String valueStr = device.valueProperty().get();
                    //if (valueStr == null || valueStr.equals("-")) return true; // xem như lỗi
                    try {
                        double value = Double.parseDouble(valueStr.replaceAll("[^\\d.]", ""));

                        return switch (type) {
                            case "TEMPERATURE" -> value < 18 || value > 30;
                            case "HUMIDITY" -> value < 35 || value > 70;
                            case "LIGHT" -> value < 200 || value > 1500;
                            case "CO2" -> value > 1500;
                            default -> false;
                        };
                    } catch (Exception e) {
                        return false;
                    }
                });

                if (hasWarning) {
                    setStyle("-fx-background-color: #FCA5A5;");
                } else {
                    setStyle(""); // reset
                }

                //System.out.println("Phòng: " + classroom.getRoomNumber() + " có thiết bị: " + devices.size());
                devices.forEach(d -> System.out.println(d.getType() + ": " + d.valueProperty().get()));

            }
        });


        return table;
    }

    private <T> TableColumn<Classroom, T> createCenteredColumn(String title, String property) {
        TableColumn<Classroom, T> col = new TableColumn<>(title);
        Label label = new Label(title);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setMinWidth(90);
        label.setAlignment(Pos.CENTER_RIGHT);
        //col.setGraphic(label);

        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                //setAlignment(Pos.CENTER_LEFT);
            }
        });

        return col;
    }


    private TableColumn<Classroom, String> createStatusToggleColumn(String title) {
        String translatedTitle = switch (title) {
            case "ACTIVE" -> "Hoạt động";
            case "INACTIVE", "MAINTENANCE" -> "Tạm ngưng";
            default -> title;
        };
        TableColumn<Classroom, String> col = createCenteredColumn(translatedTitle, "status");
        col.setCellFactory(column -> new TableCell<>() {
            private final Button statusButton = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Classroom classroom = getTableRow().getItem();
                    statusButton.setText(classroom.getFormattedStatus());
                    statusButton.getStyleClass().add("status-button");

                    if ("Tạm ngưng".equals(classroom.getFormattedStatus())) {
                        statusButton.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E");
                    } else {
                        statusButton.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46");
                    }


                    statusButton.setOnAction(e -> {
                        if ("ACTIVE".equals(classroom.statusProperty().get())) {
                            classroom.setStatus("INACTIVE");
                            statusButton.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E");
                        } else {
                            classroom.setStatus("ACTIVE");
                            statusButton.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46");
                        }
                    });
                    setGraphic(statusButton);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        return col;
    }

    private TableColumn<Classroom, Void> createActionColumn() {
        TableColumn<Classroom, Void> col = new TableColumn<>("Thao tác");
        col.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button();
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox pane = new HBox(10);

            {
                // Tạo icon
                FontIcon viewIcon = new FontIcon(FontAwesomeSolid.EYE);
                FontIcon editIcon = new FontIcon(FontAwesomeSolid.PEN); // Hoặc PENCIL_ALT
                FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);

                viewIcon.setIconSize(16);
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);

                viewIcon.setIconColor(Color.web("#1E293B"));
                editIcon.setIconColor(Color.web("#0E7490"));
                deleteIcon.setIconColor(Color.web("#DC2626"));

                viewBtn.setGraphic(viewIcon);
                editBtn.setGraphic(editIcon);
                deleteBtn.setGraphic(deleteIcon);

                viewBtn.getStyleClass().add("icon-button");
                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("icon-button");

                pane.setAlignment(Pos.CENTER);
                pane.getChildren().addAll(viewBtn, editBtn, deleteBtn);

                // Hành động
                viewBtn.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        refreshView();
                        showClassroomDetailView(getTableRow().getItem());
                    }
                });

                editBtn.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {

                        showEditClassroomView(getTableRow().getItem());

                    }
                });

                deleteBtn.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        Classroom classroom = getTableRow().getItem();
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Xác nhận xóa");
                        confirm.setHeaderText("Bạn có chắc chắn muốn xóa phòng " + classroom.getId() + "?");
                        confirm.setContentText("Hành động này không thể hoàn tác.");

                        Optional<ButtonType> result = confirm.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            List<Device> affectedDevices = DataService.getAllDevices().stream()
                                    .filter(d -> classroom.getRoomNumber().equals(d.getRoom()))
                                    .collect(Collectors.toList());

                            ApiService api = new ApiService();
                            List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

                            for (Device device : affectedDevices) {
                                UpdateDeviceRequest updateReq = new UpdateDeviceRequest(
                                        device.getName(),
                                        device.getType(),
                                        "INACTIVE",
                                        device.getDataCycle() != null ? device.getDataCycle() : 60,
                                        device.getNotes() != null ? device.getNotes() : "",
                                        null);
                                CompletableFuture<Void> updateFuture = api.updateDevice(device.getDeviceId(), updateReq);
                                updateFutures.add(updateFuture);
                                //System.out.println("Update device " + device.getDeviceId() + updateReq.getClassroomId());

                            }

                            CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0]))
                                    .thenRun(() -> {
                                        try {
                                            // Delay nhỏ để đảm bảo backend xử lý xong các update
                                            Thread.sleep(300); // 0.3s
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    })
                                    .thenCompose(v -> api.deleteClassroom(classroom.getClassroomId()))
                                    .thenRun(() -> Platform.runLater(() -> {
                                        DataService.getAllClassrooms().remove(classroom);
                                        refreshView();
                                        new Alert(Alert.AlertType.INFORMATION, "Xóa phòng học và hủy gán thiết bị thành công!").show();
                                    }))
                                    .exceptionally(ex -> {
                                        Platform.runLater(() -> {
                                            ex.printStackTrace();
                                            new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa phòng học: " + ex.getMessage()).show();
                                        });
                                        return null;
                                    });
                        }
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Classroom classroom = getTableRow().getItem();
                    classroom.statusProperty().addListener((obs, oldStatus, newStatus) -> updateButtons(newStatus));
                    updateButtons(classroom.statusProperty().get());
                    setGraphic(pane);
                    setAlignment(Pos.CENTER);
                }
            }

            private void updateButtons(String status) {
                if ("ACTIVE".equals(status)) {
                    viewBtn.setDisable(false);
                    editBtn.setDisable(true);
                    deleteBtn.setDisable(true);
                } else {
                    viewBtn.setDisable(true);
                    editBtn.setDisable(false);
                    deleteBtn.setDisable(false);
                }
            }
        });

        return col;
    }


    private void showClassroomDetailView(Classroom classroom) {
        ClassroomDetailView detailView = new ClassroomDetailView(classroom);
        HBox infoBox = detailView.getInfoBox();
        Parent detailContent = detailView.getContent();

        Button backButton = new Button("Quay lại danh sách");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showClassroomListView());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerBox = new HBox(infoBox, spacer, backButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getStyleClass().add("classroom-detail-info-box");

        VBox detailContainer = new VBox(10, headerBox, new Separator(), detailContent);
        detailContainer.setPadding(new Insets(20));
        VBox.setVgrow(detailContent, Priority.ALWAYS);

        mainContentPane.getChildren().setAll(detailContainer);
    }

    private void showClassroomListView() {
        //this.classroomListView = createClassroomListView();
        // Tái tạo view mới
        mainContentPane.getChildren().setAll(classroomListView); // Set lại
    }

    private void showCreateNewClassroomView() {
        // Tạo callback để khi tạo xong, quay về list view và refresh
        Runnable onBackCallback = () -> {
            Platform.runLater(() -> {
                refreshView();         // Tạo lại list view mới nhất
                showClassroomListView();  // Hiển thị list view
            });
        };

        ClassroomCreationView creationView = new ClassroomCreationView(onBackCallback);
        Parent creationRoot = creationView.getView();

        VBox creationContainer = new VBox(creationRoot);
        creationContainer.setPadding(new Insets(20));

        mainContentPane.getChildren().setAll(creationContainer);
    }

    private void showEditClassroomView(Classroom classroom) {
        Runnable onBackCallback = () -> {
            // Quay lại màn hình danh sách phòng
            Platform.runLater(() -> {
                refreshView();  // Tạo lại list view, lấy dữ liệu mới nhất
                showClassroomListView(); // Đổi UI về list view
            });
        };

        ClassroomEditView editView = new ClassroomEditView(onBackCallback, classroom);
        Parent editRoot = editView.getView();

        VBox editContainer = new VBox(editRoot);
        editContainer.setPadding(new Insets(20));
        mainContentPane.getChildren().setAll(editContainer);
    }


    public void refreshView() {

        this.classroomListView = createClassroomListView();
        this.mainContentPane.getChildren().setAll(this.classroomListView);
    }

}