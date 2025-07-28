package com.example.smartroom.controller;

import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;
import com.example.smartroom.service.UserSession;
import com.example.smartroom.util.ResourceLoader;
import com.example.smartroom.view.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainViewController {

    private StackPane contentArea;

    public void showMainStage() {
        Stage mainStage = new Stage();
        mainStage.setTitle("Hệ thống Sensor Room");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1600, 900); // Tăng kích thước cửa sổ
        ResourceLoader.loadCSS(scene, "/styles/style.css");

        try {
            root.setTop(createHeader());

            BorderPane mainContentPane = new BorderPane();
            VBox sideBar = createSideBar();
            mainContentPane.setLeft(sideBar);

            contentArea = new StackPane();
            contentArea.setPadding(new Insets(0)); // Bỏ padding của StackPane
            mainContentPane.setCenter(contentArea);

            root.setCenter(mainContentPane);

            showView(createDashboardView());

        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Lỗi Khởi Tạo Giao Diện");
            errorAlert.setHeaderText("Đã xảy ra lỗi nghiêm trọng khi hiển thị cửa sổ chính.");
            errorAlert.setContentText("Chi tiết lỗi:\n" + e.getMessage());
            errorAlert.showAndWait();
            return;
        }

        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.show();
    }

    private void showView(Node view) {
        contentArea.getChildren().setAll(view);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header-pane");
        header.setAlignment(Pos.CENTER_LEFT);

        Text logo = new Text("Hệ thống Sensor Room");
        logo.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        User currentUser = UserSession.getInstance().getUser();
        Label userLabel = new Label("👤 " + currentUser.fullName() + " (" + currentUser.role() + ")");

        header.getChildren().addAll(logo, spacer, userLabel);
        return header;
    }

    private VBox createSideBar() {
        VBox sideBar = new VBox(10);
        sideBar.getStyleClass().add("sidebar");
        sideBar.setPrefWidth(250);

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton dashboardButton = createSidebarButton("📊", "Dashboard", toggleGroup);
        ToggleButton roomsButton = createSidebarButton("🏛️", "Phòng học", toggleGroup);
        ToggleButton devicesButton = createSidebarButton("🔌", "Thiết bị", toggleGroup);

        dashboardButton.setSelected(true);

        dashboardButton.setOnAction(e -> showView(createDashboardView()));
        roomsButton.setOnAction(e -> showView(new ClassroomManagementView().getView()));
        devicesButton.setOnAction(e -> showView(new DeviceManagementView().getView()));

        sideBar.getChildren().addAll(dashboardButton, roomsButton, devicesButton);
        return sideBar;
    }

    private ToggleButton createSidebarButton(String iconText, String buttonText, ToggleGroup group) {
        Label icon = new Label(iconText);
        icon.getStyleClass().add("sidebar-icon");

        ToggleButton button = new ToggleButton(buttonText);
        button.setGraphic(icon);
        button.setToggleGroup(group);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private Node createDashboardView() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser.role() == Role.ADMIN) {
            return new AdminDashboardView().getView();
        } else {
            return new KtvDashboardView().getView();
        }
    }
}