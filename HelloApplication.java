package com.example.loginform;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ResourceBundle;

public class HelloApplication extends Application {
static double ii=0.0;
    private static Stage loginStage;

    public static void setLoginStage(Stage loginStage) {
        HelloApplication.loginStage = loginStage;
    }

    public static Stage getLoginStage() {
        return loginStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        openWindow(stage);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        launch();
        }

   private void openWindow(Stage stage) throws IOException {
       setLoginStage(stage);
       DBUtils.changeScene(null,null,"login-form.fxml","Welcome",null);
   }
}