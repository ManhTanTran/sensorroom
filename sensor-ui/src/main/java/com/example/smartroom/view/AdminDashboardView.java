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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class AdminDashboardView {

    private StackPane mainContentPane;


    public Node getView() {
        mainContentPane = new StackPane();
        showAdminDashboard();
        return mainContentPane;
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
        Node pieChart = createRoomQualityPieChart(DataService.getAllClassrooms());

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

        ObservableList<Device> masterDeviceList = DataService.getAllDevices();
        ObservableList<Classroom> masterClassroomList = DataService.getAllClassrooms();

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
        ObservableList<Classroom> activeRooms = masterClassroomList.filtered(c -> "Hoạt động".equals(c.statusProperty().get()));
        roomSelector.setItems(activeRooms);
        roomSelector.setPromptText("Chọn phòng học");
        roomSelector.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Classroom c) {
                return c == null ? "" : c.getId();
            }

            @Override
            public Classroom fromString(String s) {
                return null;
            }
        });
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
        Node topRoomsChart = createTopQualityRoomsChart(masterClassroomList);
        Node mostAlertsChart = createMostAlertsRoomsChart(masterClassroomList);
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
                        String.format(" %s (%d)", data.getName(), (int) data.getPieValue()),
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

        PieChart chart = new PieChart(DataService.getAlertsByTypeDistribution());
        chart.setLabelsVisible(false);
        chart.setLegendVisible(false);

        Circle donutHole = new Circle();
        donutHole.setFill(Color.WHITE);
        donutHole.radiusProperty().bind(
                Bindings.min(chart.widthProperty(), chart.heightProperty()).divide(3.5)
        );
        StackPane donutPane = new StackPane(chart, donutHole);
        VBox.setVgrow(donutPane, Priority.ALWAYS);

        HBox customLegend = new HBox();
        customLegend.getStyleClass().add("custom-legend-hbox");
        String[] colors = {"#004A7C", "#007BFF", "#74B4E0", "#A0AEC0"};
        int i = 0;
        for (PieChart.Data data : chart.getData()) {
            data.getNode().getStyleClass().add("default-color" + i);
            customLegend.getChildren().add(createLegendItem(
                    String.format(" %s(%d)  ", data.getName(), (int) data.getPieValue()),
                    colors[i % colors.length])
            );
            i++;
        }

        container.getChildren().addAll(title, donutPane, customLegend);
        return container;
    }

    Node createLegendItem(String text, String color) {
        HBox item = new HBox(0);
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
        title.setStyle("-fx-font-size: 15px;\n" +
                "    -fx-font-weight: 700;\n" +
                "    -fx-text-fill: #1E293B;");

        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Chất lượng (%)");
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Phòng học");
        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        barChart.getData().add(series);

        // Hàm cập nhật
        Runnable updateChart = () -> series.setData(DataService.getTopQualityRooms(classroomList));

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
        NumberAxis yAxis = new NumberAxis(0, 10, 2);
        yAxis.setLabel("Số lần cảnh báo");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.getData().add(series);

        // Hàm cập nhật
        Runnable updateChart = () -> {
            ObservableList<XYChart.Data<String, Number>> data = DataService.getMostAlertsRooms(classroomList);
            series.setData(data);
            // Cập nhật thang đo Y
            int maxAlerts = data.stream().mapToInt(d -> d.getYValue().intValue()).max().orElse(10);
            ((NumberAxis)yAxis).setUpperBound(Math.ceil((maxAlerts + 1) / 5.0) * 5);
        };

        updateChart.run();
        classroomList.addListener((ListChangeListener<Classroom>) c -> updateChart.run());
        DataService.getAlertHistory().addListener((ListChangeListener<AlertHistory>) c -> updateChart.run());

        VBox.setVgrow(barChart, Priority.ALWAYS);
        container.getChildren().addAll(title, barChart);
        return container;
    }
}