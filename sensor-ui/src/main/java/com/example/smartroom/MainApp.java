package com.example.smartroom;

import com.example.smartroom.view.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("App is closing...");
        Platform.exit(); // yêu cầu JavaFX kết thúc
        System.exit(0);  // đảm bảo JVM dừng
    }

    public static void main(String[] args) {
        launch(args);
    }
}