package com.example.smartroom.view;

import com.example.smartroom.model.AlertHistory;
import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.DeviceData;
import com.example.smartroom.service.DataService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KtvSingleRoomDashboardView {

    private final AdminDashboardView adminViewHelper = new AdminDashboardView();
    private final Classroom managedRoom;

    public KtvSingleRoomDashboardView(Classroom managedRoom) {
        DataService.loadAllDeviceDataFromApi();
        this.managedRoom = DataService.getUpdatedClassroomByIdFromApi(managedRoom.getId());

        // Nếu dùng API mỗi lần:
        // this.managedRoom = DataService.getUpdatedClassroomByIdFromApi(managedRoom.getId());

        if (this.managedRoom != null) {
            this.managedRoom.postProcess(); // đảm bảo có cảm biến
        } else {
            System.err.println("Không tìm thấy classroom có ID: " + managedRoom.getId());
        }
    }

    public Node getView() {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));

        HBox topSection = new HBox(10);
        VBox leftCardsPanel = createLeftCardsPanel();
        Node pieChart = createQualityPieChartByTime(managedRoom);
        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);
        leftCardsPanel.setPrefWidth(800);

        HBox bottomCharts = new HBox(10);
        Node topTimesChart = createTopQualityTimesChart();
        Node alertTimesChart = createMostAlertsByTimeChart();
        Node alertsByTypeDonut = createAlertsByTypeDonutChart(managedRoom);

        bottomCharts.getChildren().addAll(topTimesChart, alertTimesChart, alertsByTypeDonut);
        bottomCharts.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        mainLayout.getChildren().addAll(topSection, bottomCharts);
        return mainLayout;
    }

    private VBox createLeftCardsPanel() {
        VBox container = new VBox(20);
        container.setMinWidth(750);
        //ObservableList<Device> devicesInRoom = DataService.getDevicesForKtv(List.of(Long.parseLong(managedRoom.getId())));
        ObservableList<Device> devicesInRoom = FXCollections.observableArrayList(managedRoom.getDevicesInRoom());

        double temp = managedRoom.getTemperature();
        double humidity = managedRoom.getHumidity();
        double lux = managedRoom.getLux();
        double co2 = managedRoom.getCo2();

        HBox topRowCards = new HBox(20);
        Node deviceCard = adminViewHelper.createDynamicInfoCard(Bindings.size(devicesInRoom).asString(), "Tổng cảm biến");
        Node roomCard = adminViewHelper.createInfoCard(managedRoom.getRoomNumber(), "Phòng học");
        String roomName = managedRoom.getRoomNumber(); // hoặc getId() nếu bạn dùng mã phòng
        System.out.println("→ Số bản ghi của " + roomName + ": " +
                DataService.getAllDeviceData().stream()
                        .filter(d -> d.getClassroomName().trim().equalsIgnoreCase(roomName.trim()))
                        .count());

        long warningCount = DataService.getAlertHistory().stream()
                .filter(alert -> {
                    String deviceId = alert.deviceIdProperty().get();
                    // Tìm thiết bị tương ứng để lấy roomId
                    return DataService.getAllDevices().stream()
                            .filter(dev -> dev.getDeviceId().equals(deviceId))
                            .anyMatch(dev -> dev.getClassroomId() != null &&
                                    dev.getRoom().equals(managedRoom.getRoomNumber()));
                })
                .count();

        Node alertCard = adminViewHelper.createInfoCard(String.valueOf(warningCount), "Tổng cảnh báo");


        //Node alertCard = adminViewHelper.createInfoCard(String.valueOf(alertCount), "Tổng cảnh báo");
        Node statusCard = createStatusCard(managedRoom);
        topRowCards.getChildren().addAll(deviceCard, roomCard, alertCard, statusCard);
        topRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        HBox bottomRowCards = new HBox(20);

        Node co2Card = adminViewHelper.createInfoCard(String.valueOf((int) co2), "CO2 (ppm)");
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

    private Node createQualityPieChartByTime(Classroom room) {
        VBox container = new VBox(10);
        container.setMaxWidth(600);
        container.setMaxHeight(350);
        container.getStyleClass().add("chart-card");

        Label title = new Label("Phân bố chất lượng theo thời gian");
        title.getStyleClass().add("chart-title");

        ComboBox<String> timeFilter = new ComboBox<>(FXCollections.observableArrayList("1 giờ qua", "1 ngày qua", "1 tuần qua"));
        timeFilter.setValue("1 ngày qua");

        VBox legendColumn = new VBox(10);
        legendColumn.setMinWidth(120);

        VBox filterAndLegendBox = new VBox(30, timeFilter, legendColumn);
        filterAndLegendBox.setPadding(new Insets(20, 0, 0, 0));

        HBox contentBox = new HBox(0);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        PieChart chart = new PieChart();
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);

        contentBox.getChildren().addAll(filterAndLegendBox, chart);
        HBox.setHgrow(chart, Priority.ALWAYS);
        container.getChildren().addAll(title, contentBox);

        // COLORS
        String[] colors = {"#004A7C", "#007BFF", "#74B4E0"};

        // Hàm cập nhật dữ liệu
        Runnable updateChart = () -> {
            Duration duration = switch (timeFilter.getValue()) {
                case "1 giờ qua" -> Duration.ofHours(1);
                case "1 tuần qua" -> Duration.ofDays(7);
                default -> Duration.ofDays(1);
            };

            // Lọc dữ liệu đo theo thời gian và phòng
            List<DeviceData> filteredData = DataService.getAllDeviceData().stream()
                    .filter(d -> room.getRoomNumber().equals(d.getClassroomName()))
                    .filter(d -> {
                        try {
                            LocalDateTime createdAt;
                            try {
                                createdAt = LocalDateTime.parse(d.getCreatedAt());
                            } catch (Exception ex) {
                                System.out.println("Lỗi parse: " + d.getCreatedAt());
                                return false;
                            }

                            return createdAt.isAfter(LocalDateTime.now().minus(duration));
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            System.out.println("Tổng số device data: " + DataService.getAllDeviceData().size());
            System.out.println("Lọc theo room: " + room.getRoomNumber());
            System.out.println("Sau lọc thời gian: " + filteredData.size());


            // Phân loại chất lượng
            Map<DataService.AirQuality, Long> counts = filteredData.stream()
                    .map(d -> {
                        Classroom tempRoom = new Classroom();
                        tempRoom.setTemperature(d.getTemperature() != null ? d.getTemperature() : 0);
                        tempRoom.setHumidity(d.getHumidity() != null ? d.getHumidity() : 0);
                        tempRoom.setLux(d.getLight() != null ? d.getLight() : 0);
                        tempRoom.setCo2(d.getCo2() != null ? d.getCo2() : 0);
                        return DataService.getAirQuality(tempRoom);
                    })
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Đặt lại dữ liệu chart
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (DataService.AirQuality quality : DataService.AirQuality.values()) {
                long count = counts.getOrDefault(quality, 0L);
                pieData.add(new PieChart.Data(quality.name(), count));
            }

            chart.setData(pieData);

// Đợi PieChart render xong Node thì mới style
            Platform.runLater(() -> {
                legendColumn.getChildren().clear();
                for (int i = 0; i < pieData.size(); i++) {
                    PieChart.Data data = pieData.get(i);
                    Node node = data.getNode();
                    if (node != null) {
                        node.setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
                    }
                    legendColumn.getChildren().add(
                            adminViewHelper.createLegendItem(
                                    String.format(" %s (%.2f %s) ", data.getName(), data.getPieValue() * 100 / filteredData.size(), "%"),
                                    colors[i % colors.length]
                            )
                    );
                }
            });
        };

        timeFilter.setOnAction(e -> updateChart.run());
        updateChart.run(); // lần đầu

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
        series.setName("Chất lượng theo giờ");
        Runnable update = () -> {
            series.setData(DataService.getTopQualityTimes(managedRoom));

        };
        update.run();
        // Nếu future cần auto-refresh sau alert update bạn có thể nghe event:
        DataService.getAllDeviceData().addListener((ListChangeListener<DeviceData>) c -> update.run());
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
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(true);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.valueOf(object.intValue());
            }
        });

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Runnable update = () -> {
            series.setData(DataService.getMostAlertsByTime(managedRoom));
        };
        update.run();
        DataService.getAlertHistory().addListener((ListChangeListener<AlertHistory>) c -> update.run());
        barChart.getData().add(series);

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.setMaxWidth(400);
        container.getChildren().addAll(title, barChart);
        return container;
    }


    private Node createAlertsByTypeDonutChart(Classroom room) {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Số cảnh báo trên từng loại thiết bị");
        title.getStyleClass().add("chart-title");
        HBox contentBox = new HBox(10);

        PieChart chart = new PieChart(DataService.getAlertsByTypeDistributionForRoom(room));
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

        VBox customLegend = new VBox(20);
        customLegend.setPadding(new Insets(80, 0, 0, 0));
        customLegend.setMinWidth(120);
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

        contentBox.getChildren().addAll(customLegend, donutPane);
        container.setMaxWidth(400);
        container.getChildren().addAll(title, contentBox);
        return container;
    }
}