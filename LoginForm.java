package com.example.loginform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginForm implements Initializable {

    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnSignUp;
    @FXML
    private Label lbexitLable;

    private static Stage stageLoginForm;

    public static void setStageLoginForm(Stage stageLoginForm) {
        LoginForm.stageLoginForm = stageLoginForm;
    }
    public static Stage getStageLoginForm(){
        return stageLoginForm;
    }

    private Stage getStage(ActionEvent actionEvent){
       return  (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    setStageLoginForm(getStage(actionEvent));
                   DBUtils.logInUser(actionEvent,tfUsername.getText().trim(),tfPassword.getText().trim());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnSignUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    setStageLoginForm(getStage(actionEvent));
                    DBUtils.changeScene(actionEvent,LoginForm.getStageLoginForm(),"signup-form.fxml","Sign up!",null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

}
