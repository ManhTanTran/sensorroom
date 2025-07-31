package com.example.smartroom.view;

import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.service.DataService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class KtvSingleRoomDashboardView {

    private final AdminDashboardView adminViewHelper = new AdminDashboardView();
    private final Classroom managedRoom;

    public KtvSingleRoomDashboardView(Classroom managedRoom) {
        this.managedRoom = managedRoom;
    }

    public Node getView() {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));

        HBox topSection = new HBox(10);
        VBox leftCardsPanel = createLeftCardsPanel();
        Node pieChart = createQualityPieChartByTime();
        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);
        leftCardsPanel.setPrefWidth(800);

        HBox bottomCharts = new HBox(10);
        Node topTimesChart = createTopQualityTimesChart();
        Node alertTimesChart = createMostAlertsByTimeChart();
        Node alertsByTypeDonut = createAlertsByTypeDonutChart();

        bottomCharts.getChildren().addAll(topTimesChart, alertTimesChart, alertsByTypeDonut);
        bottomCharts.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        mainLayout.getChildren().addAll(topSection, bottomCharts);
        return mainLayout;
    }

    private VBox createLeftCardsPanel() {
        VBox container = new VBox(20);
        container.setMinWidth(750);
        ObservableList<Device> devicesInRoom = DataService.getDevicesForKtv(List.of(managedRoom.getId()));

        HBox topRowCards = new HBox(20);
        Node deviceCard = adminViewHelper.createDynamicInfoCard(Bindings.size(devicesInRoom).asString(), "Tổng cảm biến");
        Node roomCard = adminViewHelper.createInfoCard(managedRoom.getId(), "Phòng học");
        Node alertCard = adminViewHelper.createInfoCard("3", "Tổng cảnh báo");
        Node statusCard = createStatusCard(managedRoom);
        topRowCards.getChildren().addAll(deviceCard, roomCard, alertCard, statusCard);
        topRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        HBox bottomRowCards = new HBox(20);

        double temp = managedRoom.getTemperature();
        double humidity = managedRoom.getHumidity();
        double lux = managedRoom.getLux();
        int co2 = managedRoom.getCo2();

        Node co2Card = adminViewHelper.createInfoCard(String.valueOf(co2), "CO2 (ppm)");
        Node humidityCard = adminViewHelper.createInfoCard(String.valueOf((int) humidity), "Độ ẩm (%)");
        Node tempCard = adminViewHelper.createInfoCard(String.valueOf((int) temp), "Nhiệt độ (°C)");
        Node luxCard = adminViewHelper.createInfoCard(String.valueOf((int) lux), "Ánh sáng (lux)");


        if (co2 > 1500) {
            co2Card.getStyleClass().add("status-canh-bao");
        }
        if (humidity < 35 || humidity > 70) {
            humidityCard.getStyleClass().add("status-canh-bao");
        }
        if (temp < 18 || temp > 30) {
            tempCard.getStyleClass().add("status-canh-bao");
        }
        if (lux < 200 || lux > 1500) {
            luxCard.getStyleClass().add("status-canh-bao");
        }

        bottomRowCards.getChildren().addAll(co2Card, humidityCard, tempCard, luxCard);
        bottomRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        container.getChildren().addAll(topRowCards, bottomRowCards);
        return container;
    }

    private Node createStatusCard(Classroom room) {
        VBox card = new VBox(10);
        DataService.AirQuality quality = DataService.getAirQuality(room);
        boolean isOverThreshold = DataService.isAnyReadingOverThreshold(room);

        String styleClass = switch (quality) {
            case TỐT -> "status-hoat-ong";      // Style màu xanh
            case KHÁ -> "status-tam-ngung"; // Style màu vàng
            case KÉM -> "status-mat-ket-noi";   // Style màu đỏ
        };

        card.getStyleClass().addAll("info-card", styleClass);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        Label titleLabel = new Label("Chất lượng phòng");
        titleLabel.getStyleClass().add("info-card-title");
        Label valueLabel = new Label(quality.toString().replace("_", " "));
        valueLabel.getStyleClass().add("info-card-value");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Node createQualityPieChartByTime() {
        VBox container = new VBox(10);
        container.setMaxWidth(600);
        container.setMaxHeight(350);
        container.getStyleClass().add("chart-card");

        Label title = new Label("Phân bố chất lượng theo thời gian");
        title.getStyleClass().add("chart-title");

        ComboBox<String> timeFilter = new ComboBox<>(FXCollections.observableArrayList("1 giờ qua", "1 ngày qua", "1 tuần qua"));
        timeFilter.setValue("1 ngày qua");

        VBox legendColumn = new VBox(10);
        legendColumn.setMinWidth(200);

        VBox filterAndLegendBox = new VBox(30, timeFilter, legendColumn);
        filterAndLegendBox.setPadding(new Insets(20, 0, 0, 0));

        HBox contentBox = new HBox(0);
        contentBox.setAlignment(Pos.CENTER_LEFT);



        PieChart chart = new PieChart(FXCollections.observableArrayList(
                new PieChart.Data("Tốt", 70),
                new PieChart.Data("Trung bình", 20),
                new PieChart.Data("Kém", 10)
        ));
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);

        String[] colors = {"#004A7C", "#007BFF", "#74B4E0"};
        int i = 0;
        for (PieChart.Data data : chart.getData()) {
            data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
            legendColumn.getChildren().add(adminViewHelper.createLegendItem(
                    String.format(" %s (%d%%) ", data.getName(), (int) data.getPieValue()),
                    colors[i % colors.length])
            );
            i++;
        }

        contentBox.getChildren().addAll(filterAndLegendBox, chart);
        HBox.setHgrow(chart, Priority.ALWAYS);

        container.getChildren().addAll(title, contentBox);
        //VBox.setVgrow(contentBox, Priority.ALWAYS);
        return container;
    }

    private Node createTopQualityTimesChart() {
        VBox container = new VBox(15);
        container.setPrefWidth(400);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Top thời gian chất lượng tốt");
        title.getStyleClass().add("chart-title");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Chất lượng (%)");
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Thời gian");

        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setData(DataService.getTopQualityTimes());
        barChart.getData().add(series);

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.setMaxWidth(400);
        container.getChildren().addAll(title, barChart);
        return container;
    }

    private Node createMostAlertsByTimeChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Phân bố cảnh báo theo thời gian");
        title.getStyleClass().add("chart-title");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Thời gian");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lần cảnh báo");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setData(DataService.getMostAlertsByTime());
        barChart.getData().add(series);

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.setMaxWidth(400);
        container.getChildren().addAll(title, barChart);
        return container;
    }


    private Node createAlertsByTypeDonutChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Số cảnh báo trên từng loại thiết bị");
        title.getStyleClass().add("chart-title");

        PieChart chart = new PieChart(DataService.getAlertsByTypeDistribution());
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);

        Circle donutHole = new Circle();
        donutHole.setFill(Color.WHITE);
        donutHole.radiusProperty().bind(
                Bindings.min(
                        chart.widthProperty().divide(3.5),
                        chart.heightProperty().divide(3.5)
                )
        );

        StackPane donutPane = new StackPane(chart, donutHole);
        VBox.setVgrow(donutPane, Priority.ALWAYS);

        HBox customLegend = new HBox();
        customLegend.getStyleClass().add("custom-legend-hbox");
        String[] colors = {"#004A7C", "#007BFF", "#74B4E0", "#A0AEC0"};
        int i = 0;
        for (PieChart.Data data : chart.getData()) {
            data.getNode().getStyleClass().add("default-color" + i);
            customLegend.getChildren().add(adminViewHelper.createLegendItem(
                    String.format(" %s(%d)  ", data.getName(), (int) data.getPieValue()),
                    colors[i % colors.length])
            );
            i++;
        }
        container.setMaxWidth(400);
        container.getChildren().addAll(title, donutPane, customLegend);
        return container;
    }
}