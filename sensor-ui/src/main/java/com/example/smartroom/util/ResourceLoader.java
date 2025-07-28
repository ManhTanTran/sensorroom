package com.example.smartroom.util;

import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.Objects;

public final class ResourceLoader {

    private ResourceLoader() {}

    public static void loadCSS(Scene scene, String cssPath) {
        try {
            URL cssUrl = Objects.requireNonNull(ResourceLoader.class.getResource(cssPath),
                    "Không thể tìm thấy tệp CSS tại đường dẫn: " + cssPath);
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("LỖI NGHIÊM TRỌNG: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi Tải Giao Diện");
            alert.setHeaderText("Không thể tải tệp giao diện cần thiết.");
            alert.setContentText("Chi tiết: " + e.getMessage() + "\n\nVui lòng đảm bảo cấu trúc thư mục dự án là chính xác.");
            alert.showAndWait();
        }
    }
}