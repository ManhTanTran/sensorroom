package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.User;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class KtvDashboardView {

    private final AdminDashboardView adminViewHelper = new AdminDashboardView();
    private StackPane contentPane;

    public Node getView() {
        contentPane = new StackPane();
        showMultiRoomDashboard();
        return contentPane;
    }

    private void showMultiRoomDashboard() {
        VBox multiRoomView = new VBox(30);
        multiRoomView.setPadding(new Insets(30));

        User currentUser = UserSession.getInstance().getUser();

        // Lấy danh sách gốc và tạo danh sách đã lọc một cách an toàn
        ObservableList<Device> allDevices = DataService.getAllDevices();
        ObservableList<Classroom> allClassrooms = DataService.getAllClassrooms();

        // SỬA LỖI LOGIC TẠI ĐÂY: Thêm kiểm tra null cho device.getRoom()
        ObservableList<Device> ktvDevices = allDevices.filtered(
                device -> device.getRoom() != null && currentUser.managedRooms().contains(device.getRoom())
        );
        ObservableList<Classroom> managedClassrooms = allClassrooms.filtered(
                classroom -> currentUser.managedRooms().contains(classroom.getRoomNumber())
        );

        // --- HBOX TRÊN CÙNG ---
        HBox topSection = new HBox(30);

        VBox leftCardsPanel = createLeftCardsPanel(ktvDevices, managedClassrooms);

        Node pieChart = adminViewHelper.createRoomQualityPieChart(managedClassrooms);

        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);

        // --- HBOX DƯỚI CÙNG ---
        HBox bottomSection = new HBox(30);
        Node topRoomsChart = adminViewHelper.createTopQualityRoomsChart(managedClassrooms);
        Node mostAlertsChart = adminViewHelper.createMostAlertsRoomsChart(managedClassrooms);
        Node alertsByTypeChart = adminViewHelper.createAlertsByTypeDonutChart();

        bottomSection.getChildren().addAll(topRoomsChart, mostAlertsChart, alertsByTypeChart);
        bottomSection.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        multiRoomView.getChildren().addAll(topSection, bottomSection);
        VBox.setVgrow(bottomSection, Priority.ALWAYS);

        contentPane.getChildren().setAll(multiRoomView);
    }

    private VBox createLeftCardsPanel(ObservableList<Device> ktvDevices, ObservableList<Classroom> managedClassrooms) {
        VBox container = new VBox(20);
        container.setPrefWidth(750);

        HBox topRowCards = new HBox(20);
        Node deviceCard = adminViewHelper.createDynamicInfoCard(Bindings.size(ktvDevices).asString(), "Tổng cảm biến");
        Node roomCard = adminViewHelper.createDynamicInfoCard(Bindings.size(managedClassrooms).asString(), "Tổng phòng");
        Node alertCard = adminViewHelper.createInfoCard(String.valueOf(DataService.getAlertHistory().size()), "Tổng cảnh báo");
        topRowCards.getChildren().addAll(deviceCard, roomCard, alertCard);
        topRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        HBox bottomRowCards = new HBox(20);
        bottomRowCards.getChildren().addAll(
                adminViewHelper.createInfoCard("--", "CO2 (ppm)"),
                adminViewHelper.createInfoCard("--", "Độ ẩm (%)"),
                adminViewHelper.createInfoCard("--", "Nhiệt độ (°C)"),
                adminViewHelper.createInfoCard("--", "Ánh sáng (lux)")
        );
        bottomRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        ComboBox<Classroom> roomSelector = new ComboBox<>(managedClassrooms);
        roomSelector.setPromptText("Chọn phòng để xem chi tiết...");
        roomSelector.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Classroom c) { return c == null ? "" : c.getId(); }
            @Override public Classroom fromString(String s) { return null; }
        });
        roomSelector.setOnAction(e -> {
            Classroom selected = roomSelector.getValue();
            if (selected != null) {
                showSingleRoomDashboard(selected);
            }
        });

        Label valueLabel = new Label("Chất lượng môi trường:");
        valueLabel.getStyleClass().add("chart-title");
        HBox filterBox = new HBox(20, valueLabel, roomSelector);
        filterBox.setPadding(new Insets(10, 0, 0, 0));

        container.getChildren().addAll(topRowCards, filterBox, bottomRowCards);
        return container;
    }

    private void showSingleRoomDashboard(Classroom room) {
        KtvSingleRoomDashboardView singleView = new KtvSingleRoomDashboardView(room);

        FontIcon backIcon = new FontIcon(FontAwesomeSolid.ARROW_LEFT);
        backIcon.setIconSize(16);
        backIcon.setIconColor(Color.BLACK); // hoặc #334155 nếu dùng nền sáng

        Label backLabel = new Label("Quay lại tổng quan");
        backLabel.setTextFill(Color.BLACK); // đổi màu theo background bạn dùng
        backLabel.setFont(Font.font(14));

        HBox backContent = new HBox(8, backIcon, backLabel);
        backContent.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button();
        backButton.setGraphic(backContent);

        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showMultiRoomDashboard());

        VBox container = new VBox(20, backButton, singleView.getView());
        container.setPadding(new Insets(30));

        contentPane.getChildren().setAll(container);
    }
}