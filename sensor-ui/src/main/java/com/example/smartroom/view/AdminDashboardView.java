package com.example.smartroom.view;

import com.example.smartroom.model.AlertHistory;
import com.example.smartroom.model.Classroom;
import com.example.smartroom.model.Device;
import com.example.smartroom.model.DeviceData;
import com.example.smartroom.service.ApiService;
import com.example.smartroom.service.DataService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AdminDashboardView {

    private StackPane mainContentPane;
    // --- THÊM CÁC BIẾN ĐỂ LƯU DỮ LIỆU TỪ API ---
    private final ApiService apiService = new ApiService();
    private final ObservableList<Device> masterDeviceList = FXCollections.observableArrayList();
    private final ObservableList<Classroom> masterClassroomList = FXCollections.observableArrayList();
    private final ObservableList<AlertHistory> masterAlertList = FXCollections.observableArrayList();


    public Node getView() {
        mainContentPane = new StackPane();
        loadInitialData();
        return mainContentPane;
    }

    /**
     * Phương thức mới để tải dữ liệu từ API
     */
    private void loadInitialData() {
        CompletableFuture<ObservableList<Device>> devicesFuture = apiService.fetchDevices();
        CompletableFuture<ObservableList<Classroom>> classroomsFuture = apiService.fetchClassrooms();
        CompletableFuture<List<DeviceData>> deviceDataFuture = apiService.fetchAllDeviceData();

        CompletableFuture.allOf(devicesFuture, classroomsFuture, deviceDataFuture).thenRun(() -> {
            Platform.runLater(() -> {
                masterDeviceList.setAll(devicesFuture.join());
                masterClassroomList.setAll(classroomsFuture.join());

                // GÁN DANH SÁCH DEVICE VÀO MỖI CLASSROOM
                for (Classroom classroom : masterClassroomList) {
                    List<Device> devicesInRoom = masterDeviceList.stream()
                            .filter(d -> classroom.getRoomNumber().equals(d.getRoom()))
                            .toList();
                    classroom.setDevicesInRoom(devicesInRoom);
                }

                // GÁN GIÁ TRỊ ĐO MỚI NHẤT CHO TỪNG CLASSROOM
                Map<String, DeviceData> latestDataMap = deviceDataFuture.join().stream()
                        .collect(Collectors.toMap(DeviceData::getDeviceId, data -> data, (d1, d2) -> d1));

                masterClassroomList.forEach(classroom -> {
                    for (Device d : classroom.getDevicesInRoom()) {
                        DeviceData data = latestDataMap.get(d.getDeviceId());
                        if (data == null) continue;

                        switch (d.getType()) {
                            case "TEMPERATURE" -> classroom.setTemperature(data.getTemperature());
                            case "HUMIDITY" -> classroom.setHumidity(data.getHumidity());
                            case "LIGHT" -> classroom.setLux(data.getLight());
                            case "CO2" -> classroom.setCo2(data.getCo2());
                        }
                    }
                });

                //DataService.loadAllDevicesFromApi(); // 👈 RẤT QUAN TRỌNG
                DataService.getAllDeviceData();
                DataService.loadAllDevicesFromApi();
                DataService.loadAllDeviceDataFromApi();
                DataService.regenerateAlertsFromDeviceData();

                DataService.setAllDevices(masterDeviceList);
                DataService.setAllClassrooms(masterClassroomList);
                // đảm bảo masterAlertList phản ánh toàn bộ alert hệ thống
                masterAlertList.setAll(DataService.getAlertHistory());
                // khi DataService cập nhật getAlertHistory(), cập nhật masterAlertList theo
                DataService.getAlertHistory().addListener((ListChangeListener<AlertHistory>) change -> {
                    Platform.runLater(() -> masterAlertList.setAll(DataService.getAlertHistory()));
                });

                showAdminDashboard();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Label errorLabel = new Label("Không thể tải dữ liệu từ server: " + ex.getMessage());
                mainContentPane.getChildren().setAll(errorLabel);
            });
            return null;
        });



    }



    private void showAdminDashboard() {
        VBox adminLayout = new VBox(30);
        adminLayout.setPadding(new Insets(30));

        HBox topSection = createTopSection();
        HBox bottomSection = createBottomSection();

        adminLayout.getChildren().addAll(topSection, bottomSection);
        VBox.setVgrow(bottomSection, Priority.ALWAYS);

        mainContentPane.getChildren().setAll(adminLayout);
    }

    private void showKtvSingleRoomView(Classroom room) {
        KtvSingleRoomDashboardView singleView = new KtvSingleRoomDashboardView(room);

        FontIcon backIcon = new FontIcon(FontAwesomeSolid.ARROW_LEFT);
        backIcon.setIconSize(16);
        backIcon.setIconColor(Color.BLACK); // hoặc #334155 nếu dùng nền sáng

        Label backLabel = new Label("Quay lại Dashboard Admin");
        backLabel.setTextFill(Color.BLACK); // đổi màu theo background bạn dùng
        backLabel.setFont(Font.font(14));

        HBox backContent = new HBox(8, backIcon, backLabel);
        backContent.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button();
        backButton.setGraphic(backContent);

        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showAdminDashboard());

        VBox container = new VBox(10, backButton, singleView.getView());
        container.setPadding(new Insets(10));

        mainContentPane.getChildren().setAll(container);
    }

    private HBox createTopSection() {
        HBox topSection = new HBox(30);
        VBox leftCardsPanel = createLeftCardsPanel();
        Node pieChart = createRoomQualityPieChart(masterClassroomList);


        // SỬA LẠI BỐ CỤC TẠI ĐÂY
        // Cho VBox thẻ chiếm ưu thế và đẩy PieChart
        HBox.setHgrow(leftCardsPanel, Priority.ALWAYS);

        topSection.getChildren().addAll(leftCardsPanel, pieChart);
        return topSection;
    }

    private VBox createLeftCardsPanel() {
        VBox container = new VBox(20);

        // Đặt chiều rộng ưa thích cho VBox để nó "phình" ra
        container.setPrefWidth(750);

        //ObservableList<Device> masterDeviceList = DataService.getAllDevices();
        //ObservableList<Classroom> masterClassroomList = DataService.getAllClassrooms();

        ObservableList<Device> masterDeviceList = this.masterDeviceList;
        ObservableList<Classroom> masterClassroomList = this.masterClassroomList;

        HBox topRowCards = new HBox(20);
        Node deviceCard = createDynamicInfoCard(Bindings.size(masterDeviceList).asString(), "Tổng cảm biến");
        Node roomCard = createDynamicInfoCard(Bindings.size(masterClassroomList).asString(), "Tổng phòng");
        Node alertCard = createInfoCard(String.valueOf(DataService.getAlertHistory().size()), "Tổng cảnh báo");
        topRowCards.getChildren().addAll(deviceCard, roomCard, alertCard);
        topRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        HBox bottomRowCards = new HBox(20);
        bottomRowCards.getChildren().addAll(
                createInfoCard("--", "CO2 (ppm)"),
                createInfoCard("--", "Độ ẩm (%)"),
                createInfoCard("--", "Nhiệt độ (°C)"),
                createInfoCard("--", "Ánh sáng (lux)")
        );
        bottomRowCards.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        ComboBox<Classroom> roomSelector = new ComboBox<>();


        FilteredList<Classroom> activeRooms = new FilteredList<>(masterClassroomList);
        activeRooms.setPredicate(c -> "ACTIVE".equals(c.statusProperty().get()));

// Khi statusProperty của bất kỳ Classroom nào thay đổi, cập nhật lại predicate
        masterClassroomList.forEach(c ->
                c.statusProperty().addListener((obs, oldVal, newVal) -> {
                    activeRooms.setPredicate(c2 -> "ACTIVE".equals(c2.statusProperty().get()));
                })
        );

        roomSelector.setItems(activeRooms);
        roomSelector.setPromptText("Chọn phòng học");

// Hiển thị ID phòng học trong ComboBox
        roomSelector.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Classroom c) {
                return c == null ? "" : c.getRoomNumber();
            }

            @Override
            public Classroom fromString(String s) {
                return null;
            }
        });

// Mở dashboard khi chọn phòng
        roomSelector.setOnAction(e -> {
            Classroom selected = roomSelector.getValue();
            if (selected != null) {
                showKtvSingleRoomView(selected);
            }
        });


        Label valueLabel = new Label("Chất lượng môi trường:");
        valueLabel.getStyleClass().add("chart-title");
        HBox filterBox = new HBox(20, valueLabel, roomSelector);
        filterBox.setPadding(new Insets(10, 0, 0, 0));

        container.getChildren().addAll(topRowCards, filterBox, bottomRowCards);
        return container;
    }

    private HBox createBottomSection() {
        HBox bottomSection = new HBox(30);
        ObservableList<Classroom> masterClassroomList = DataService.getAllClassrooms();

        // Truyền danh sách phòng vào các phương thức tạo biểu đồ
        Node topRoomsChart = createTopQualityRoomsChart(this.masterClassroomList);
        Node mostAlertsChart = createMostAlertsRoomsChart(this.masterClassroomList);

        Node alertsByTypeChart = createAlertsByTypeDonutChart();

        bottomSection.getChildren().addAll(topRoomsChart, mostAlertsChart, alertsByTypeChart);
        bottomSection.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));
        return bottomSection;
    }

    Node createDynamicInfoCard(javafx.beans.value.ObservableStringValue valueProperty, String title) {
        Node card = createInfoCard("", title);
        Label valueLabel = (Label) ((VBox) card).getChildren().get(1);
        valueLabel.textProperty().bind(valueProperty);
        return card;
    }

    public Node createInfoCard(String value, String title) {
        VBox card = new VBox(5);
        card.getStyleClass().add("info-card");
        card.setAlignment(Pos.CENTER);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-card-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("info-card-value");
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    public Node createRoomQualityPieChart(ObservableList<Classroom> classroomList) {
        VBox container = new VBox(15);
        container.setMaxWidth(600);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Phân bố chất lượng không khí");

        title.setMinWidth(250);
        title.setWrapText(false);
        title.getStyleClass().add("chart-title");
        title.setAlignment(Pos.CENTER);

        HBox contentBox = new HBox(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        VBox legendColumn = new VBox(20);
        legendColumn.setPadding(new Insets(100, 0, 0, 0));
        legendColumn.setPrefWidth(350);

        PieChart chart = new PieChart();
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);
        chart.setAnimated(true);

        Runnable updateChart = () -> {
            chart.setData(DataService.getRoomQualityDistribution(classroomList));
            legendColumn.getChildren().clear();
            legendColumn.getChildren().add(title);
            String[] colors = {"#004A7C", "#007BFF", "#74B4E0"};
            for (PieChart.Data data : chart.getData()) {
                String color = switch (data.getName()) {
                    case "TỐT" -> colors[0];
                    case "TRUNG BÌNH" -> colors[1];
                    case "KÉM" -> colors[2];
                    default -> "#A0AEC0";
                };
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
                legendColumn.getChildren().add(createLegendItem(
                        String.format(" %s (%.2f%s)", data.getName(), data.getPieValue() * 100 /classroomList.size(), "%"),
                        color)
                );
            }
        };

        updateChart.run();
        classroomList.addListener((ListChangeListener<Classroom>) c -> updateChart.run());
        for (Classroom classroom : classroomList) {
            classroom.statusProperty().addListener((obs, oldVal, newVal) -> updateChart.run());
        }

        contentBox.getChildren().addAll(legendColumn, chart);
        HBox.setHgrow(chart, Priority.ALWAYS);
        container.getChildren().addAll(title, contentBox);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        return container;
    }

    public Node createAlertsByTypeDonutChart() {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Phân bố cảnh báo");
        title.setAlignment(Pos.CENTER);
        title.getStyleClass().add("chart-title");

        HBox contentBox = new HBox(10);

        PieChart chart = new PieChart();
        Runnable updateChart = () -> {
            chart.setData(DataService.getAlertsByTypeDistribution());
        };
        updateChart.run();
        DataService.getAlertHistory().addListener((ListChangeListener<AlertHistory>) change -> updateChart.run());

        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);


        Circle donutHole = new Circle();
        donutHole.setFill(Color.WHITE);
        donutHole.radiusProperty().bind(
                Bindings.min(chart.widthProperty(), chart.heightProperty()).divide(3.5)
        );
        StackPane donutPane = new StackPane(chart, donutHole);
        VBox.setVgrow(donutPane, Priority.ALWAYS);

        VBox customLegend = new VBox(20);
        customLegend.setMinWidth(120);
        customLegend.setPadding(new Insets(70, 0, 0, 0));
        customLegend.getStyleClass().add("custom-legend-hbox");
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

        chart.setAnimated(false);

        contentBox.getChildren().addAll(customLegend, donutPane);

        container.getChildren().addAll(title, contentBox);
        return container;
    }

    Node createLegendItem(String text, String color) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);

        Rectangle colorRect = new Rectangle(14, 14);
        colorRect.setArcWidth(4);
        colorRect.setArcHeight(4);
        colorRect.setFill(Color.web(color));

        Label label = new Label(text);
        label.getStyleClass().add("custom-legend-item-label");

        item.getChildren().addAll(colorRect, label);
        return item;
    }


    public Node createTopQualityRoomsChart(ObservableList<Classroom> classroomList) {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Top phòng học theo đánh giá chất lượng không khí");
        title.setMinWidth(300);
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-font-weight: 700;\n" +
                "    -fx-text-fill: #1E293B;");

        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Chất lượng (%)");
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Phòng học");
        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        barChart.getData().add(series);

        // Hàm cập nhật
        Runnable updateChart = () -> {
            ObservableList<XYChart.Data<Number, String>> chartData = DataService.getTopQualityRooms(classroomList);
            if (chartData != null && !chartData.isEmpty()) {
                series.setData(chartData);
            } else {
                series.getData().clear(); // Tránh lỗi nếu dữ liệu rỗng
            }
        };

        updateChart.run(); // Chạy lần đầu
        classroomList.addListener((ListChangeListener<Classroom>) c -> updateChart.run());

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.getChildren().addAll(title, barChart);
        return container;
    }

    public Node createMostAlertsRoomsChart(ObservableList<Classroom> classroomList) {
        VBox container = new VBox(15);
        container.getStyleClass().add("chart-card");
        Label title = new Label("Phân bố thiết bị có cảnh báo theo từng phòng học");
        title.setMinWidth(300);
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 15px;\n" +
                "    -fx-font-weight: 700;\n" +
                "    -fx-text-fill: #1E293B;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Phòng học");

        NumberAxis yAxis = new NumberAxis(0, 100, 2);
        yAxis.setLabel("Số lần cảnh báo");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.getData().add(series);

        Runnable updateChart = () -> {
            ObservableList<XYChart.Data<String, Number>> alertData = DataService.getMostAlertsRooms(classroomList);
            int totalRooms = classroomList.size();

            // Lấy top 5 phòng có cảnh báo nhiều nhất
            List<String> topAlertRooms = alertData.stream()
                    .map(XYChart.Data::getXValue)
                    .collect(Collectors.toList());

            // Nếu tổng phòng > 5 thì ta hiển thị 5 phòng này, còn không thì lấy hết phòng
            List<String> categories;
            if (totalRooms > 5) {
                // Nếu topAlertRooms < 5 thì bổ sung thêm phòng từ classroomList để đủ 5 phòng
                categories = new ArrayList<>(topAlertRooms);
                for (Classroom c : classroomList) {
                    if (categories.size() >= 5) break;
                    if (!categories.contains(c.getRoomNumber())) {
                        categories.add(c.getRoomNumber());
                    }
                }
            } else {
                // Hiển thị tất cả phòng khi tổng phòng <= 5
                categories = classroomList.stream()
                        .map(Classroom::getRoomNumber)
                        .collect(Collectors.toList());
            }

            xAxis.setCategories(FXCollections.observableArrayList(categories));

            // Chuẩn bị data cho series, đảm bảo tất cả phòng trong categories có dữ liệu (0 nếu ko có)
            List<XYChart.Data<String, Number>> dataWithZero = categories.stream()
                    .map(room -> {
                        // Tìm dữ liệu cảnh báo cho phòng này
                        XYChart.Data<String, Number> d = alertData.stream()
                                .filter(ad -> ad.getXValue().equals(room))
                                .findFirst()
                                .orElse(new XYChart.Data<>(room, 0));
                        return d;
                    }).collect(Collectors.toList());

            series.setData(FXCollections.observableArrayList(dataWithZero));

            // Cập nhật thang đo Y dựa trên dữ liệu hiện tại
            int maxAlerts = dataWithZero.stream()
                    .mapToInt(d -> d.getYValue().intValue())
                    .max()
                    .orElse(10);
            ((NumberAxis) yAxis).setUpperBound(Math.ceil((maxAlerts + 1) / 5.0) * 5);
        };

        updateChart.run();

        classroomList.addListener((ListChangeListener<Classroom>) c -> updateChart.run());
        DataService.getAlertHistory().addListener((ListChangeListener<AlertHistory>) c -> updateChart.run());

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.getChildren().addAll(title, barChart);
        return container;
    }

}