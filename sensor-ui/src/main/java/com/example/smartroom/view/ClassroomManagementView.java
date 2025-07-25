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
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        layout.setPadding(new Insets(10, 0, 0, 0));

        User currentUser = UserSession.getInstance().getUser();
        ObservableList<Classroom> classroomData = (currentUser.role() == Role.ADMIN)
                ? DataService.getAllClassrooms()
                : DataService.getClassroomsForKtv(currentUser.managedRooms());

        FilteredList<Classroom> filteredData = new FilteredList<>(classroomData, p -> true);
        HBox topBox = createFilterBox(filteredData);
        TableView<Classroom> classroomTable = createClassroomTable(filteredData);

        layout.getChildren().addAll(topBox, classroomTable);
        return layout;
    }

    private HBox createFilterBox(FilteredList<Classroom> filteredData) {
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getStyleClass().add("filter-box");

        TextField idFilter = new TextField();
        idFilter.setPromptText("Mã phòng");

        TextField roomNumberFilter = new TextField();
        roomNumberFilter.setPromptText("Số phòng");

        ComboBox<String> buildingFilter = new ComboBox<>();
        buildingFilter.setPromptText("Tòa nhà");
        buildingFilter.getItems().addAll("Tất cả", "Tòa A", "Tòa B", "Tòa C");
        buildingFilter.setValue("Tất cả");

        Button searchButton = new Button("🔍");
        searchButton.getStyleClass().add("search-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createButton = new Button("➕ Tạo mới");
        createButton.getStyleClass().add("create-button");
        createButton.setOnAction(e -> showCreateNewClassroomView());

        HBox searchFields = new HBox(10, new Label("Tìm kiếm:"), idFilter, roomNumberFilter, buildingFilter, searchButton);
        searchFields.setAlignment(Pos.CENTER_LEFT);

        filterBox.getChildren().addAll(searchFields, spacer, createButton);

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

        TableColumn<Classroom, String> sttCol = createCenteredColumn("STT", null);
        sttCol.setCellFactory(col -> {
            TableCell<Classroom, String> cell = new TableCell<>();
            cell.textProperty().bind(Bindings.createStringBinding(() -> {
                if (cell.isEmpty() || cell.getTableRow() == null) return null;
                return Integer.toString(cell.getTableRow().getIndex() + 1);
            }, cell.emptyProperty(), cell.tableRowProperty()));
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        TableColumn<Classroom, String> idCol = createCenteredColumn("Mã phòng", "id");
        TableColumn<Classroom, String> numberCol = createCenteredColumn("Số phòng", "roomNumber");
        TableColumn<Classroom, String> buildingCol = createCenteredColumn("Tòa nhà", "building");
        TableColumn<Classroom, String> floorCol = createCenteredColumn("Tầng", "floor");
        TableColumn<Classroom, String> typeCol = createCenteredColumn("Loại phòng", "roomType");
        TableColumn<Classroom, Integer> deviceCountCol = createCenteredColumn("Số lượng thiết bị", "deviceCount");
        TableColumn<Classroom, String> createdDateCol = createCenteredColumn("Ngày tạo", "creationDate");
        TableColumn<Classroom, String> statusCol = createStatusToggleColumn("Trạng thái");
        TableColumn<Classroom, Void> actionCol = createActionColumn();

        sttCol.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        numberCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        buildingCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        floorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        typeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        deviceCountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        createdDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

        table.getColumns().setAll(sttCol, idCol, numberCol, buildingCol, floorCol, typeCol, deviceCountCol, createdDateCol, statusCol, actionCol);

        return table;
    }

    private <T, S> TableColumn<T, S> createCenteredColumn(String title, String property) {
        TableColumn<T, S> col = new TableColumn<>(title);
        if (property != null) {
            col.setCellValueFactory(new PropertyValueFactory<>(property));
        }
        col.setCellFactory(c -> {
            TableCell<T, S> cell = new TableCell<>() {
                @Override
                protected void updateItem(S item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return col;
    }

    private TableColumn<Classroom, String> createStatusToggleColumn(String title) {
        TableColumn<Classroom, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>("status"));

        col.setCellFactory(column -> new TableCell<>() {
            private final Button statusButton = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Lấy đối tượng Classroom của hàng hiện tại
                    Classroom classroom = getTableView().getItems().get(getIndex());

                    // Cập nhật text và style cho nút
                    statusButton.setText(item);
                    statusButton.getStyleClass().setAll("status-button", "status-" + item.toLowerCase().replace(" ", "-"));

                    // Gán hành động cho nút
                    statusButton.setOnAction(e -> {
                        if ("Hoạt động".equals(classroom.statusProperty().get())) {
                            classroom.setStatus("Tạm ngưng");
                        } else {
                            classroom.setStatus("Hoạt động");
                        }
                        // Không cần gọi refresh, vì thuộc tính thay đổi sẽ tự động cập nhật
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
        col.setCellFactory(param -> {
            final TableCell<Classroom, Void> cell = new TableCell<>() {
                private final Button viewBtn = new Button("👁️");
                {
                    viewBtn.getStyleClass().add("action-button-view");
                    viewBtn.setOnAction(event -> {
                        Classroom selectedClassroom = getTableView().getItems().get(getIndex());
                        showClassroomDetailView(selectedClassroom);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : viewBtn);
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return col;
    }
    /*
    private void showClassroomDetailView(Classroom classroom) {
        ClassroomDetailView detailView = new ClassroomDetailView(classroom);
        Parent detailRoot = detailView.getView();

        Button backButton = new Button("Quay lại danh sách");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showClassroomListView());

        HBox header = new HBox(backButton);
        header.setPadding(new Insets(0, 0, 10, 0));

        VBox detailContainer = new VBox(10, header, detailRoot);
        detailContainer.setPadding(new Insets(20));
        VBox.setVgrow(detailRoot, Priority.ALWAYS);

        mainContentPane.getChildren().setAll(detailContainer);
    }
    */

    private void showClassroomDetailView(Classroom classroom) {
        // 1. Tạo instance của view chi tiết
        ClassroomDetailView detailView = new ClassroomDetailView(classroom);

        // 2. Lấy về 2 phần riêng biệt: thanh thông tin và nội dung chính
        HBox infoBox = detailView.getInfoBox();
        Parent detailContent = detailView.getContent();

        // 3. Tạo nút "Quay lại"
        Button backButton = new Button("Quay lại danh sách");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showClassroomListView());

        // 4. Tạo một spacer để đẩy nút "Quay lại" sang phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 5. Gộp thanh thông tin, spacer, và nút "Quay lại" vào một HBox header
        HBox headerBox = new HBox(infoBox, spacer, backButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        // 6. Tạo VBox cuối cùng để chứa header và nội dung chính
        VBox detailContainer = new VBox(10, headerBox, detailContent);
        detailContainer.setPadding(new Insets(20));
        VBox.setVgrow(detailContent, Priority.ALWAYS);

        // 7. Hiển thị container này trên StackPane
        mainContentPane.getChildren().setAll(detailContainer);
    }
    private void showClassroomListView() {
        mainContentPane.getChildren().setAll(classroomListView);
    }

    /**
     * PHƯƠNG THỨC MỚI: Hiển thị view tạo mới trên StackPane
     */
    private void showCreateNewClassroomView() {
        // Tạo view tạo mới và truyền vào một callback để nó có thể quay lại
        ClassroomCreationView creationView = new ClassroomCreationView(this::showClassroomListView);
        Parent creationRoot = creationView.getView();

        // Bọc trong một VBox để có padding
        VBox creationContainer = new VBox(creationRoot);
        creationContainer.setPadding(new Insets(20));

        mainContentPane.getChildren().setAll(creationContainer);
    }
}