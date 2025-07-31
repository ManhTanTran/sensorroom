package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import com.example.smartroom.util.ResourceLoader;
import javafx.beans.binding.Bindings;
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

import java.util.Optional;

public class ClassroomManagementView {

    private StackPane mainContentPane;
    private Node classroomListView;

    public Node getView() {
        mainContentPane = new StackPane();
        classroomListView = createClassroomListView();
        mainContentPane.getChildren().add(classroomListView);
        return mainContentPane;
    }

    private Node createClassroomListView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("management-view-container");

        User currentUser = UserSession.getInstance().getUser();
        ObservableList<Classroom> classroomData = (currentUser.role() == Role.ADMIN)
                ? DataService.getAllClassrooms()
                : DataService.getClassroomsForKtv(currentUser.managedRooms());

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
        filterBox.getStyleClass().add("filter-pane");

        filterBox.getChildren().add(new Label("Tìm kiếm:"));

        TextField idFilter = new TextField();
        idFilter.setPromptText("Mã phòng");

        TextField roomNumberFilter = new TextField();
        roomNumberFilter.setPromptText("Số phòng");

        ComboBox<String> buildingFilter = new ComboBox<>();
        buildingFilter.setPromptText("Tòa nhà");
        buildingFilter.getItems().addAll("Tất cả", "Tòa A", "Tòa B", "Tòa C");
        buildingFilter.setValue("Tất cả");

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(14);
        Button searchButton = new Button("", searchIcon);

        FontIcon plusIcon = new FontIcon(FontAwesomeSolid.PLUS);
        plusIcon.setIconSize(14);
        plusIcon.setIconColor(Color.WHITE);
        Button createButton = new Button(" Tạo mới", plusIcon);
        createButton.setContentDisplay(ContentDisplay.LEFT);
        createButton.getStyleClass().add("create-button");
        createButton.setOnAction(e -> showCreateNewClassroomView());


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);



        filterBox.getChildren().addAll(idFilter, roomNumberFilter, buildingFilter, searchButton, spacer, createButton);

        searchButton.setOnAction(e -> {
            String idVal = idFilter.getText().trim().toLowerCase();
            String roomNumVal = roomNumberFilter.getText().trim().toLowerCase();
            String buildingVal = buildingFilter.getValue();

            filteredData.setPredicate(classroom -> {
                boolean idMatch = idVal.isEmpty() || classroom.idProperty().get().toLowerCase().contains(idVal);
                boolean roomNumMatch = roomNumVal.isEmpty() || classroom.getRoomNumber().toLowerCase().contains(roomNumVal);
                boolean buildingMatch = buildingVal == null || buildingVal.equals("Tất cả") || classroom.getBuilding().equals(buildingVal);
                return idMatch && roomNumMatch && buildingMatch;
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
        TableColumn<Classroom, String> typeCol = createCenteredColumn("Loại phòng", "roomType");
        TableColumn<Classroom, Integer> deviceCountCol = createCenteredColumn("Số Thiết Bị", "deviceCount");
        TableColumn<Classroom, String> createdDateCol = createCenteredColumn("Ngày tạo", "creationDate");

        TableColumn<Classroom, String> statusCol = createStatusToggleColumn("Trạng thái");
        TableColumn<Classroom, Void> actionCol = createActionColumn();

        table.getColumns().setAll(
                sttCol, idCol, numberCol, buildingCol, floorCol,
                typeCol, deviceCountCol, createdDateCol, statusCol, actionCol
        );

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
        TableColumn<Classroom, String> col = createCenteredColumn(title, "status");
        col.setCellFactory(column -> new TableCell<>() {
            private final Button statusButton = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Classroom classroom = getTableRow().getItem();
                    statusButton.setText(item);
                    statusButton.getStyleClass().add("status-button");
                    if ("Tạm ngưng".equals(classroom.statusProperty().get())) {
                        statusButton.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E");
                    } else {
                        statusButton.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46");
                    }


                    statusButton.setOnAction(e -> {
                        if ("Hoạt động".equals(classroom.statusProperty().get())) {
                            classroom.setStatus("Tạm ngưng");
                            statusButton.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E");
                        } else {
                            classroom.setStatus("Hoạt động");
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
                            DataService.getAllClassrooms().remove(classroom);
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
                if ("Hoạt động".equals(status)) {
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
        mainContentPane.getChildren().setAll(classroomListView);
    }

    private void showCreateNewClassroomView() {
        ClassroomCreationView creationView = new ClassroomCreationView(this::showClassroomListView);
        Parent creationRoot = creationView.getView();

        VBox creationContainer = new VBox(creationRoot);
        creationContainer.setPadding(new Insets(20));

        mainContentPane.getChildren().setAll(creationContainer);
    }

    private void showEditClassroomView(Classroom classroom) {
        ClassroomEditView editView = new ClassroomEditView(this::showClassroomListView, classroom);
        Parent editRoot = editView.getView();

        VBox editContainer = new VBox(editRoot);
        editContainer.setPadding(new Insets(20));

        mainContentPane.getChildren().setAll(editContainer);
    }
}