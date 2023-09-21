package com.example.loginform;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Authorization implements Initializable {
        @FXML
        private Button btnSetPassword;

        @FXML
        private  PasswordField pfManagerPassword;

        @FXML
        private Stage newstage;

    static double pbIndicator=0.0;

    public void setNewstage(Stage newstage) {
        this.newstage = newstage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSetPassword.setOnAction(actionEvent -> {
          if(pfManagerPassword.getText().equals("000")) {
              newstage.close();
          }
            else {
              DBUtils.showErrorMessage("Incorrect Password!");
          }
        });
    }
    public static void openAuthorizationDialog() {
        FXMLLoader loader = new FXMLLoader(Authorization.class.getResource("Authorization.fxml"));
        Parent root = null;

        try {
            root = loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProgressIndicator pb = new ProgressIndicator();
        EventHandler<ActionEvent > eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                for(int i =0;i<10;i++){
                    pbIndicator+=0.1;
                    pb.setProgress(pbIndicator);
                }
            }
        };

        AnchorPane stackPane = new AnchorPane();
        stackPane.getChildren().add(root);
        stackPane.getChildren().add(pb);


        pb.setLayoutX(456);
        pb.setLayoutY(150);

        Stage passwordStage = new Stage();
        passwordStage.setScene(new Scene(stackPane));

        Authorization authorization = loader.getController();
        authorization.setNewstage(passwordStage);

        passwordStage.initStyle(StageStyle.TRANSPARENT);
        passwordStage.initModality(Modality.APPLICATION_MODAL);
        passwordStage.showAndWait();
    }
}
