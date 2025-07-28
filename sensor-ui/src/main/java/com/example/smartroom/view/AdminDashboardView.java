package com.example.smartroom.view;

import com.example.smartroom.model.AlertHistory;
import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class AdminDashboardView {

    private ObservableList<PieChart.Data> pieChartData;

    public Node getView() {
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));

        ObservableList<Device> masterDeviceList = DataService.getAllDevices();
        ObservableList<Classroom> masterClassroomList = DataService.getAllClassrooms();

        HBox topSection = new HBox(30);
        VBox leftCardsPanel = new VBox(20);

        HBox topRowCards = new HBox(20);
        Node deviceCard = createDynamicInfoCard(Bindings.size(masterDeviceList).asString(), "Tổng cảm biến", "M5...z");

        // --- LOGIC MỚI CHO CARD "TỔNG PHÒNG HOẠT ĐỘNG" ---
        var activeRoomCountBinding = Bindings.createIntegerBinding(() ->
                        (int) masterClassroomList.stream().filter(c -> "Hoạt động".equals(c.statusProperty().get())).count(),
                masterClassroomList
        );
        // Lắng nghe sự thay đổi trạng thái của từng phòng trong danh sách
        masterClassroomList.forEach(classroom -> classroom.statusProperty().addListener((obs, oldVal, newVal) -> activeRoomCountBinding.invalidate()));
        Node roomCard = createDynamicInfoCard(activeRoomCountBinding.asString(), "Tổng phòng hoạt động", "M16...H16z");

        Node alertCard = createInfoCard(String.valueOf(DataService.getAlertHistory().size()), "Tổng cảnh báo", "M11...z");
        topRowCards.getChildren().addAll(deviceCard, roomCard, alertCard);
        topRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        HBox bottomRowCards = new HBox(20);
        bottomRowCards.getChildren().addAll(
                createInfoCard("800", "CO2 (ppm)", "M1...z"),
                createInfoCard("50", "Độ ẩm (%)", "M12...18.01z"),
                createInfoCard("25", "Nhiệt độ (°C)", "M15...19z"),
                createInfoCard("350", "Ánh sáng (lux)", "M12...5z")
        );
        bottomRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        leftCardsPanel.getChildren().addAll(topRowCards, bottomRowCards);

        Node pieChart = createRoomQualityPieChart();

        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        topSection.setPrefWidth(600);
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);

        HBox bottomSection = new HBox(30);
        Node topRoomsChart = createTopQualityRoomsChart();
        Node mostAlertsChart = createMostAlertsRoomsChart();
        Node alertsByTypeChart = createAlertsByTypePieChart();

        bottomSection.getChildren().addAll(topRoomsChart, mostAlertsChart, alertsByTypeChart);
        bottomSection.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));
        bottomSection.setPrefWidth(800);

        mainLayout.getChildren().addAll(topSection, bottomSection);
        VBox.setVgrow(bottomSection, Priority.ALWAYS);

        return mainLayout;
    }

    private Node createDynamicInfoCard(javafx.beans.value.ObservableStringValue valueProperty, String title, String svgPath) {
        Node card = createInfoCard("", title, svgPath);
        Label valueLabel = (Label) ((VBox) ((BorderPane) card).getLeft()).getChildren().get(1);
        valueLabel.textProperty().bind(valueProperty);
        return card;
    }

    public Node createInfoCard(String value, String title, String svgPath) {
        BorderPane card = new BorderPane();
        card.getStyleClass().add("info-card");

        VBox textContainer = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-card-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("info-card-value");
        textContainer.getChildren().addAll(titleLabel, valueLabel);

        card.setLeft(textContainer);
        return card;
    }

    public Node createRoomQualityPieChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        container.getStyleClass().add("pie-chart-container");

        HBox contentBox = new HBox(30);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        VBox legendColumn = new VBox(20);
        legendColumn.setPrefWidth(350);

        Label title = new Label("Phân bố chất lượng không khí");
        title.getStyleClass().add("chart-title");

        VBox legendItemsBox = new VBox();
        legendItemsBox.getStyleClass().add("custom-legend-vbox");

        legendColumn.getChildren().addAll(title, legendItemsBox);

        PieChart chart = new PieChart(DataService.getRoomQualityDistribution());
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);

        String[] colors = {"#2ECC71", "#F1C40F", "#E74C3C"};
        int i = 0;
        for (PieChart.Data data : chart.getData()) {
            if (data.getNode() != null) {
                String color = switch (data.getName()) {
                    case "TỐT" -> colors[0];
                    case "TRUNG BÌNH" -> colors[1];
                    case "KÉM" -> colors[2];
                    default -> "#A0AEC0";
                };
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
                legendItemsBox.getChildren().add(createLegendItem(
                        String.format("%s (%d)", data.getName(), (int) data.getPieValue()),
                        color)
                );
            }
            i++;
        }

        contentBox.getChildren().addAll(legendColumn, chart);
        HBox.setHgrow(chart, Priority.ALWAYS);
        container.getChildren().add(contentBox);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        return container;
    }

    public Node createAlertsByTypePieChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Phân bố cảnh báo");
        title.getStyleClass().add("chart-title");

        PieChart chart = new PieChart(DataService.getAlertsByTypeDistribution());
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);

        VBox legendItemsBox = new VBox();
        legendItemsBox.getStyleClass().add("custom-legend-vbox");

        String[] colors = {"#004A7C", "#007BFF", "#74B4E0", "#A0AEC0"};
        int i = 0;
        for (PieChart.Data data : chart.getData()) {
            if (data.getNode() != null) {
                data.getNode().getStyleClass().add("default-color" + i);
            }
            legendItemsBox.getChildren().add(createLegendItem(
                    String.format("%s (%d)", data.getName(), (int) data.getPieValue()),
                    colors[i % colors.length])
            );
            i++;
        }

        container.getChildren().addAll(title, chart, legendItemsBox);
        VBox.setVgrow(chart, Priority.ALWAYS);

        return container;
    }

    private Node createLegendItem(String text, String color) {
        HBox item = new HBox();
        item.getStyleClass().add("custom-legend-item");

        Rectangle colorRect = new Rectangle();
        colorRect.getStyleClass().add("color-rect");
        colorRect.setFill(Color.web(color));
        colorRect.setWidth(14);
        colorRect.setHeight(14);
        colorRect.setArcWidth(3);
        colorRect.setArcHeight(3);

        Label label = new Label(text);
        label.setWrapText(true);

        item.getChildren().addAll(colorRect, label);
        return item;
    }

    public Node createTopQualityRoomsChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("5 phòng có chất lượng tốt nhất");
        title.getStyleClass().add("chart-title");

        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Chất lượng (%)");
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Phòng học");
        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setData(DataService.getTopQualityRooms());
        barChart.getData().add(series);

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.getChildren().addAll(title, barChart);
        return container;
    }

    public Node createMostAlertsRoomsChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("5 phòng có số cảnh báo nhiều nhất");
        title.getStyleClass().add("chart-title");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Phòng học");
        NumberAxis yAxis = new NumberAxis(0, 10, 2);
        yAxis.setLabel("Số lần cảnh báo");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setData(DataService.getMostAlertsRooms());
        barChart.getData().add(series);

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.getChildren().addAll(title, barChart);
        return container;
    }
}