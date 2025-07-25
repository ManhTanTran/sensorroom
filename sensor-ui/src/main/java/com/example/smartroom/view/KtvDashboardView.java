package com.example.smartroom.view;

import com.example.smartroom.model.Device;
import com.example.smartroom.model.User;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class KtvDashboardView {
    // Helper để tái sử dụng các phương thức TẠO THÀNH PHẦN từ AdminDashboardView
    private final AdminDashboardView adminViewHelper = new AdminDashboardView();

    public Node getView() {
        // Sử dụng ScrollPane để đảm bảo không bao giờ bị tràn
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Layout chính là một VBox, chứa 2 HBox lớn (y hệt AdminDashboardView)
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));

        // Lấy danh sách thiết bị GỐC và tạo danh sách đã lọc cho KTV
        ObservableList<Device> masterDeviceList = DataService.getAllDevices();
        User currentUser = UserSession.getInstance().getUser();
        ObservableList<Device> ktvDeviceList = masterDeviceList.filtered(
                d -> currentUser.managedRooms().contains(d.getRoom())
        );

        // --- 1. HBOX TRÊN CÙNG ---
        HBox topSection = new HBox(30);

        // 1a. VBOX BÊN TRÁI chứa các thẻ thông tin
        VBox leftCardsPanel = createLeftCardsPanel(ktvDeviceList, currentUser);

        // 1b. PIE CHART BÊN PHẢI (SỬ DỤNG LẠI PHƯƠNG THỨC MỚI)
        // KTV cũng cần xem phân bố chất lượng phòng họ quản lý
        Node pieChart = adminViewHelper.createRoomQualityPieChart(); // SỬ DỤNG ĐÚNG PHƯƠNG THỨC MỚI

        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);

        // --- 2. HBOX DƯỚI CÙNG ---
        HBox bottomSection = new HBox(30);

        // Tái sử dụng các biểu đồ từ AdminDashboardView
        Node topRoomsChart = adminViewHelper.createTopQualityRoomsChart();
        Node mostAlertsChart = adminViewHelper.createMostAlertsRoomsChart();
        Node alertsByTypeChart = adminViewHelper.createAlertsByTypePieChart(); // BIỂU ĐỒ MỚI

        bottomSection.getChildren().addAll(topRoomsChart, mostAlertsChart, alertsByTypeChart);
        bottomSection.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        // Thêm 2 HBox lớn vào VBox chính
        mainLayout.getChildren().addAll(topSection, bottomSection);
        VBox.setVgrow(bottomSection, Priority.ALWAYS);

        scrollPane.setContent(mainLayout);
        return scrollPane;
    }

    private VBox createLeftCardsPanel(ObservableList<Device> ktvDeviceList, User currentUser) {
        VBox container = new VBox(20);

        // HBox con 1: 3 thẻ
        HBox topRowCards = new HBox(20);
        Node deviceCard = createDynamicInfoCard(Bindings.size(ktvDeviceList).asString(), "Tổng cảm biến", "M5...z");
        // Hiển thị số lượng phòng thay vì danh sách dài
        Node roomCard = adminViewHelper.createInfoCard(String.valueOf(currentUser.managedRooms().size()), "Tổng phòng", "M16...H16z");
        // KTV không có tổng cảnh báo, thay bằng thẻ trạng thái chung
        Node statusCard = adminViewHelper.createInfoCard("Tốt", "Trạng thái chung", "M11...z");
        topRowCards.getChildren().addAll(deviceCard, roomCard, statusCard);
        topRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        // HBox con 2: 4 thẻ
        HBox bottomRowCards = new HBox(20);
        bottomRowCards.getChildren().addAll(
                adminViewHelper.createInfoCard("800", "CO2 (ppm)", "M1...z"),
                adminViewHelper.createInfoCard("50", "Độ ẩm (%)", "M12...18.01z"),
                adminViewHelper.createInfoCard("25", "Nhiệt độ (°C)", "M15...19z"),
                adminViewHelper.createInfoCard("350", "Ánh sáng (lux)", "M12...5z")
        );
        bottomRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        container.getChildren().addAll(topRowCards, bottomRowCards);
        return container;
    }

    private Node createDynamicInfoCard(javafx.beans.value.ObservableStringValue valueProperty, String title, String svgPath) {
        Node card = adminViewHelper.createInfoCard("", title, svgPath);
        Label valueLabel = (Label) ((VBox) ((BorderPane) card).getLeft()).getChildren().get(1);
        valueLabel.textProperty().bind(valueProperty);
        return card;
    }
}