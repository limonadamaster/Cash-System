package com.example.loginform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUp implements Initializable {

@FXML
private Button btnSignUp;
@FXML
private Button btnLogIn;
@FXML
private TextField tfUsername;

@FXML
private TextField tfPassword;

private static Stage stageSignUp;

    public static Stage getStageSignUp() {
        return stageSignUp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    btnSignUp.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            if(!tfUsername.getText().trim().isEmpty()&&!tfPassword.getText().trim().isEmpty()){
                stageSignUp=(Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                DBUtils.signUpUser(actionEvent,tfUsername.getText(),tfPassword.getText());
            }else{
                Alert aler = new Alert(Alert.AlertType.ERROR);
                aler.setContentText("Already have this user!");
                aler.show();
            }
        }
    });

    btnLogIn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            stageSignUp=(Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            DBUtils.changeScene(actionEvent,SignUp.getStageSignUp(),"login-form.fxml","Log in",null);
        }
    });
    }
}
