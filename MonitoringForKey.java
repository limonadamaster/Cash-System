package com.example.loginform;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;

public class MonitoringForKey   {
    private static volatile boolean monitoring = false;

public static void setThread(Stage stage){
    Scene scene = new Scene(new javafx.scene.layout.StackPane());
    stage.setScene(scene);

    scene.addEventHandler(KeyEvent.KEY_PRESSED,keyEvent -> {
        if(keyEvent.getCode()==KeyCode.SPACE){
            System.out.println("ESC Pressed");
            monitoring=true;
            Platform.runLater(()->{
                Platform.exit();
            });
            System.exit(0);
        }
    });

    Thread escThread = new Thread(() -> {
        while (true) {
            if(monitoring) {
                break;
            }
            System.out.println("ESC key pressed in the separate thread.");
            try {
                Thread.sleep(100); // Sleep for a while to avoid busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    escThread.setDaemon(true);
    escThread.start();
}

}
