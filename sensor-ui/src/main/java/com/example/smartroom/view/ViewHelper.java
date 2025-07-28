package com.example.smartroom.view;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.Normalizer;

public class ViewHelper {

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> createStyledCellFactory(String stylePrefix) {
        return column -> new TableCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String rawText = item.toString()
                            .replaceAll("\\p{C}", "")               // loại bỏ ký tự không in được
                            .replaceAll("\\s+", " ")                // chuẩn hóa khoảng trắng
                            .trim();

                    label.setText(rawText);

                    // Xóa các style cũ bắt đầu bằng "status-"
                    label.getStyleClass().removeIf(style -> style.startsWith("status-"));

                    // Chuẩn hóa chuỗi (bỏ dấu tiếng Việt và chuyển về lowercase)
                    String normalized = Normalizer.normalize(rawText, Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // bỏ dấu
                            .replaceAll("[^\\w\\s-]", "")                        // bỏ ký tự đặc biệt (nếu có)
                            .toLowerCase()
                            .replace(" ", "-");

                    String styleClass = stylePrefix + normalized;

                    // Gán class style
                    label.getStyleClass().add("status-label");
                    label.getStyleClass().add(styleClass);

                    setGraphic(label);
                    setText(null);

                    // Debug log nếu cần
                    //System.out.println("DEBUG: '" + rawText + "' => '" + styleClass + "'");
                }
            }
        };
    }
}
