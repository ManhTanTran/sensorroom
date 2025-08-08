package com.example.smartroom.controller;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Role;
import com.example.smartroom.model.User;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import com.example.smartroom.util.ResourceLoader;
import com.example.smartroom.view.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainViewController {

    private StackPane contentArea;

    public void showMainStage() {
        Stage mainStage = new Stage();
        mainStage.setTitle("Hệ thống Smart Room");

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

    /*
    private HBox createHeader() {
        HBox header = new HBox(1000);
        header.getStyleClass().add("header-pane");
        header.setAlignment(Pos.CENTER_LEFT);

        Text logo = new Text("Hệ thống Smart Room");
        logo.getStyleClass().add("header-title");

        //Region spacer = new Region();
        //HBox.setHgrow(spacer, Priority.ALWAYS);
        User currentUser = UserSession.getInstance().getUser();
        FontIcon userIcon = new FontIcon(FontAwesomeSolid.USER);
        userIcon.setIconSize(18);
        userIcon.getStyleClass().add("sidebar-icon");

        Label userLabel = new Label(currentUser.fullName() + " (" + currentUser.role() + ")", userIcon);
        userLabel.getStyleClass().add("chart-title");
        userLabel.setGraphicTextGap(8);

        header.getChildren().addAll(logo, userLabel);
        return header;
    }*/

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.getStyleClass().add("header-pane");
        header.setAlignment(Pos.CENTER_LEFT);

        Text logo = new Text("Hệ thống Smart Room");
        logo.getStyleClass().add("header-title");

        User currentUser = UserSession.getInstance().getUser();
        FontIcon userIcon = new FontIcon(FontAwesomeSolid.USER);
        userIcon.setIconSize(18);
        userIcon.getStyleClass().add("sidebar-icon");

        Label userLabel = new Label(currentUser.fullName() + " (" + currentUser.role() + ")", userIcon);
        userLabel.getStyleClass().add("chart-title");
        userLabel.setGraphicTextGap(8);

        // Tạo nút logout với icon thoát
        FontIcon logoutIcon = new FontIcon(FontAwesomeSolid.SIGN_OUT_ALT);
        logoutIcon.setIconSize(18);
        logoutIcon.getStyleClass().add("sidebar-icon");
        Button logoutButton = new Button("", logoutIcon);
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setTooltip(new Tooltip("Đăng xuất"));
        logoutButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận đăng xuất");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Đóng cửa sổ hiện tại
                    Stage currentStage = (Stage) header.getScene().getWindow();
                    currentStage.close();

                    // Mở lại cửa sổ đăng nhập
                    Stage loginStage = new Stage();
                    new LoginView(loginStage).show();
                }
            });
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logo, spacer, userLabel, logoutButton);
        return header;
    }


    private VBox createSideBar() {
        VBox sideBar = new VBox(10);
        sideBar.getStyleClass().add("sidebar");
        sideBar.setPrefWidth(250);

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton dashboardButton = createSidebarButton(
                new FontIcon(FontAwesomeSolid.CHART_BAR), "Dashboard", toggleGroup);
        ToggleButton roomsButton = createSidebarButton(
                new FontIcon(FontAwesomeSolid.UNIVERSITY), "Phòng học", toggleGroup);
        ToggleButton devicesButton = createSidebarButton(
                new FontIcon(FontAwesomeSolid.PLUG), "Thiết bị", toggleGroup);

        dashboardButton.setSelected(true);

        dashboardButton.setOnAction(e -> showView(createDashboardView()));
        roomsButton.setOnAction(e -> showView(new ClassroomManagementView().getView()));
        devicesButton.setOnAction(e -> showView(new DeviceManagementView().getView()));

        sideBar.getChildren().addAll(dashboardButton, roomsButton, devicesButton);
        return sideBar;
    }

    private ToggleButton createSidebarButton(FontIcon icon, String buttonText, ToggleGroup group) {
        icon.setIconSize(18);
        icon.getStyleClass().add("sidebar-icon");
        icon.setIconColor(Color.web("#334155")); // default màu đen

        Label label = new Label(buttonText);
        label.getStyleClass().add("sidebar-text");
        label.setFont(Font.font(16));
        label.setTextFill(Color.web("#334155")); // default màu đen

        HBox content = new HBox(10, icon, label);
        content.setAlignment(Pos.CENTER_LEFT);

        ToggleButton button = new ToggleButton();
        button.setGraphic(content);
        button.setToggleGroup(group);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);

        // Gán icon và label vào UserData để dễ xử lý màu
        button.setUserData(new Object[]{icon, label});

        // Lắng nghe toggle thay đổi để đổi màu icon/text
        button.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            FontIcon fi = ((FontIcon) ((Object[]) button.getUserData())[0]);
            Label lb = ((Label) ((Object[]) button.getUserData())[1]);

            if (isNowSelected) {
                fi.setIconColor(Color.WHITE);
                lb.setTextFill(Color.WHITE);
            } else {
                fi.setIconColor(Color.web("#334155")); // màu gốc
                lb.setTextFill(Color.web("#334155"));
            }
        });

        return button;
    }


    private Node createDashboardView() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser.role() == Role.ADMIN) {
            return new AdminDashboardView().getView();
        } else { // KTV
            // KIỂM TRA SỐ LƯỢNG PHÒNG KTV QUẢN LÝ
            if (currentUser.managedRooms().size() > 1) {
                // KTV quản lý nhiều phòng
                currentUser.managedRooms().forEach(c -> System.out.println(c.getRoomNumber()));
                return new KtvDashboardView().getView();
            } else {
                // KTV quản lý 1 phòng
                // Lấy thông tin phòng duy nhất đó
                System.out.println(currentUser.managedRooms().get(0).getRoomNumber());

                Classroom singleRoom = DataService.getAllClassrooms().stream()
                        .filter(c -> currentUser.managedRooms().get(0).getRoomNumber().equals(c.getRoomNumber()))
                        .findFirst().orElse(null);

                if (singleRoom != null) {
                    singleRoom.postProcess();
                    return new KtvSingleRoomDashboardView(singleRoom).getView();
                } else {
                    return new Label("Lỗi: Không tìm thấy thông tin phòng học.");
                }
            }
        }
    }
}