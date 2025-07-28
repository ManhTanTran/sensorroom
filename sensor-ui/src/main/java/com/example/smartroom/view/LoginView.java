package com.example.smartroom.view;

import com.example.smartroom.controller.MainViewController;
import com.example.smartroom.model.User;
import com.example.smartroom.service.AuthenticationService;
import com.example.smartroom.service.UserSession;
import com.example.smartroom.util.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class LoginView {

    private final Stage primaryStage;
    private final AuthenticationService authService = new AuthenticationService();

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setTitle("Hệ thống sensor room - Đăng nhập");
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FFFFFF;");

        ImageView leftImageView = createLeftImage();

        VBox loginContainer = new VBox(25);
        loginContainer.getStyleClass().add("login-form-container");
        loginContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(loginContainer, Priority.ALWAYS);

        VBox formContent = new VBox(25);
        formContent.setMaxWidth(400);

        Label title = new Label("Đăng nhập hệ thống");
        title.getStyleClass().add("login-title");
        title.setPadding(new Insets(0, 0, 20, 0));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");
        usernameField.getStyleClass().add("login-input");

        StackPane passwordStackPane = new StackPane();
        passwordStackPane.getStyleClass().add("password-stack-pane");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Mật khẩu");
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        passwordField.getStyleClass().add("text-field");
        visiblePasswordField.getStyleClass().add("text-field");

        passwordStackPane.getChildren().addAll(passwordField, visiblePasswordField);

        Button showPasswordButton = new Button("👁️");
        showPasswordButton.getStyleClass().add("show-password-button");

        HBox passwordWrapper = new HBox(passwordStackPane, showPasswordButton);
        passwordWrapper.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passwordStackPane, Priority.ALWAYS);

        showPasswordButton.setOnMousePressed(e -> {
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
        });
        showPasswordButton.setOnMouseReleased(e -> {
            passwordField.setText(visiblePasswordField.getText());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordField.requestFocus();
            passwordField.end();
        });

        Button loginButton = new Button("Đăng nhập");
        loginButton.getStyleClass().add("login-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        formContent.getChildren().addAll(title, usernameField, passwordWrapper, loginButton);
        loginContainer.getChildren().add(formContent);

        root.getChildren().addAll(leftImageView, loginContainer);

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        passwordField.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        visiblePasswordField.setOnAction(e -> handleLogin(usernameField.getText(), visiblePasswordField.getText()));

        Scene scene = new Scene(root, 1200, 700);
        ResourceLoader.loadCSS(scene, "/styles/style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private ImageView createLeftImage() {
        ImageView imageView = new ImageView();
        String imagePath = "/images/channels4_profile.jpg";
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) throw new Exception("Không tìm thấy tài nguyên tại: " + imagePath);

            File imageFile = new File(imageUrl.toURI());
            if (!imageFile.exists()) throw new Exception("Tệp không tồn tại tại: " + imageFile.getAbsolutePath());

            Image bgImage = new Image(new FileInputStream(imageFile), 600, 700, false, true);
            imageView.setImage(bgImage);
            imageView.setFitHeight(700);
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(false);
        } catch (Exception e) {
            System.err.println("LỖI KHI TẢI ẢNH NỀN: " + e.getMessage());
            Region placeholder = new Region();
            placeholder.setPrefSize(600, 700);
            placeholder.setStyle("-fx-background-color: #004A7C;");
            imageView.setImage(placeholder.snapshot(null, null));
        }
        return imageView;
    }

    private void handleLogin(String username, String password) {
        User user = authService.login(username, password);
        if (user != null) {
            UserSession.getInstance(user);
            primaryStage.close();
            new MainViewController().showMainStage();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi Đăng Nhập");
            alert.setHeaderText(null);
            alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng!");
            alert.showAndWait();
        }
    }
}