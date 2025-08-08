package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.User;
import com.example.smartroom.model.AlertHistory;
import com.example.smartroom.service.DataService;
import com.example.smartroom.service.UserSession;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KtvDashboardView {

    private final AdminDashboardView adminViewHelper = new AdminDashboardView();
    private StackPane contentPane;
    private ObservableList<Classroom> managedClassrooms;
    private ObservableList<Device> managedDevices;
    private ObservableList<AlertHistory> managedAlerts;

    public Node getView() {
        contentPane = new StackPane();
        loadData();
        return contentPane;
    }

    private void loadData() {
        User currentUser = UserSession.getInstance().getUser();

        DataService.loadAllDevicesFromApi();
        DataService.loadAllDeviceDataFromApi();
        DataService.getAllClassroomsFromApi();

        System.out.println("📌 allDeviceData size = " + DataService.getAllDeviceData().size());
        /*DataService.getAllDeviceData().forEach(d ->
                System.out.println(d.getClassroomId() + " | " + d.getCreatedAt() + " | temp=" + d.getTemperature())
        );*/

        DataService.regenerateAlertsFromDeviceData();
        System.out.println("📌 alertHistory size after regenerate = " + DataService.getAlertHistory().size());

        // Lọc các phòng được quản lý
        managedClassrooms = DataService.getClassroomsForKtvFromApi(currentUser.managedRooms());
        System.out.println("📌 managedClassrooms = " + managedClassrooms.size());
        managedClassrooms.forEach(c -> System.out.println("Room: " + c.getRoomNumber()));

        // Lọc cảm biến thuộc những phòng được quản lý
        managedDevices = FXCollections.observableArrayList(
                DataService.getAllDevices().stream()
                        .filter(device -> managedClassrooms.stream()
                                .anyMatch(c -> c.getRoomNumber().equalsIgnoreCase(device.getRoom()))
                        )
                        .toList()
        );
        System.out.println("📌 managedDevices = " + managedDevices.size());
        managedDevices.forEach(d -> System.out.println("Device: " + d.getDeviceId()));

        // Lọc cảnh báo theo deviceId
        Set<String> managedDeviceIds = managedDevices.stream()
                .map(Device::getDeviceId)
                .collect(Collectors.toSet());

        managedAlerts = FXCollections.observableArrayList(
                DataService.getAlertHistory().stream()
                        .filter(alert -> managedDeviceIds.contains(alert.deviceIdProperty().get()))
                        .toList()
        );
        System.out.println("📌 managedAlerts = " + managedAlerts.size());

        showMultiRoomDashboard();
    }


    private void showMultiRoomDashboard() {
        VBox multiRoomView = new VBox(30);
        multiRoomView.setPadding(new Insets(30));

        HBox topSection = new HBox(30);
        VBox leftCardsPanel = createLeftCardsPanel();
        Node pieChart = adminViewHelper.createRoomQualityPieChart(managedClassrooms);

        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);

        HBox bottomSection = new HBox(30);
        bottomSection.getChildren().addAll(
                adminViewHelper.createTopQualityRoomsChart(managedClassrooms),
                adminViewHelper.createMostAlertsRoomsChart(managedClassrooms),
                createAlertsByTypeDonutChartForKtv(managedAlerts) // truyền alert đã lọc
        );
        bottomSection.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        multiRoomView.getChildren().addAll(topSection, bottomSection);
        VBox.setVgrow(bottomSection, Priority.ALWAYS);

        contentPane.getChildren().setAll(multiRoomView);
    }

    private VBox createLeftCardsPanel() {
        VBox container = new VBox(20);
        container.setPrefWidth(750);

        HBox topRowCards = new HBox(20);
        Node deviceCard = adminViewHelper.createDynamicInfoCard(
                Bindings.size(managedDevices).asString(), "Tổng cảm biến"
        );
        Node roomCard = adminViewHelper.createDynamicInfoCard(
                Bindings.size(managedClassrooms).asString(), "Tổng phòng"
        );
        Node alertCard = adminViewHelper.createDynamicInfoCard(
                Bindings.size(managedAlerts).asString(), "Tổng cảnh báo"
        );
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
        roomSelector.setPromptText("Chọn phòng");
        roomSelector.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Classroom c) { return c == null ? "" : c.getRoomNumber(); }
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
        backIcon.setIconColor(Color.BLACK);

        Label backLabel = new Label("Quay lại tổng quan");
        backLabel.setTextFill(Color.BLACK);
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

    private Node createAlertsByTypeDonutChartForKtv(ObservableList<AlertHistory> alerts) {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Phân bố cảnh báo (KTV)");
        title.setAlignment(Pos.CENTER);
        title.getStyleClass().add("chart-title");

        HBox contentBox = new HBox(10);

        PieChart chart = new PieChart();

        VBox customLegend = new VBox(20);
        customLegend.setMinWidth(120);
        customLegend.setPadding(new Insets(70, 0, 0, 0));
        customLegend.getStyleClass().add("custom-legend-hbox");

        Runnable updateChart = () -> {
            // Đếm số lượng alert theo type
            var grouped = alerts.stream()
                    .collect(Collectors.groupingBy(AlertHistory::getAlertType, Collectors.counting()));

            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
            grouped.forEach((type, count) -> {
                chartData.add(new PieChart.Data(type, count));
            });
            chart.setData(chartData);

            // Cập nhật custom legend
            customLegend.getChildren().clear();
            String[] colors = {"#004A7C", "#007BFF", "#74B4E0", "#A0AEC0"};
            int i = 0;
            for (PieChart.Data data : chart.getData()) {
                data.getNode().getStyleClass().add("default-color" + i);
                customLegend.getChildren().add(createLegendItem(
                        String.format(" %s (%d)  ", data.getName(), (int) data.getPieValue()),
                        colors[i % colors.length])
                );
                i++;
            }
        };

        updateChart.run();
        alerts.addListener((ListChangeListener<AlertHistory>) change -> updateChart.run());

        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        Circle donutHole = new Circle();
        donutHole.setFill(Color.WHITE);
        donutHole.radiusProperty().bind(
                Bindings.min(chart.widthProperty(), chart.heightProperty()).divide(3.5)
        );
        StackPane donutPane = new StackPane(chart, donutHole);
        VBox.setVgrow(donutPane, Priority.ALWAYS);

        contentBox.getChildren().addAll(customLegend, donutPane);
        container.getChildren().addAll(title, contentBox);
        return container;
    }

    // Hàm tạo legend item
    private HBox createLegendItem(String text, String color) {
        Region colorBox = new Region();
        colorBox.setPrefSize(15, 15);
        colorBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3px;");

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;");

        HBox item = new HBox(8, colorBox, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

}
