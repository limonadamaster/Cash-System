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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class SuccessLogin implements Initializable  {

@FXML
private Label lbWelcome;
@FXML
private Button btnLogOut;
@FXML
private ListView<Product> lvProducts;
@FXML
private TextField tfSearchProduct;
@FXML
private TextField tfSearchBill;
@FXML
private Button btnSearchProduct;
@FXML
private Button btnSell;
@FXML
private Button btnDeleteFormListView;
@FXML
private Label lbPrice;
@FXML
private Button btnSearchBill;


public int reverseProduct;

public static Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogOut.setOnAction(actionEvent -> {
            setPrimaryStage((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
            DBUtils.changeScene(actionEvent,primaryStage,"login-form.fxml","Log in",null);
            DBUtils.setLogInLogOutInformationToFile(DBUtils.getUserName()+".txt"," logged out ");
            DBUtils.setUserName("");
            DBUtils.setCashForCashier(0.0);
        }
        );
        btnSearchProduct.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                        Product product=DBUtils.searchProduct(tfSearchProduct.getText());
                        if(product.getName()!=null && product.getPrice()!=null){
                        lvProducts.getItems().add(product);
                        calculatePrice();
                        tfSearchProduct.clear();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDeleteFormListView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reverseProduct++;

                if(reverseProduct>2&&!lvProducts.getItems().isEmpty()){
                   Authorization.openAuthorizationDialog();
                }
                deleteItemFromListView(lvProducts);
                calculatePrice();
            }
        });

        btnSell.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    sellProducts();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnSearchBill.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                DBUtils.changeScene(actionEvent,SuccessLogin.primaryStage,"product-return.fxml","as",null);
            }
        });
    }

    public void setUsername(String username) {
        lbWelcome.setText("Cashier "+username);
    }

    protected void deleteItemFromListView(ListView listView){
        int selectedIndex= listView.getSelectionModel().getSelectedIndex();
        if(selectedIndex>=0){
            listView.getItems().remove(selectedIndex);
        }
    }

    private void calculatePrice(){
        Double totalPrice=0.0;
        for(Product product : lvProducts.getItems()){
            totalPrice+=Double.parseDouble(product.getPrice());;
        }
        lbPrice.setText("Price: $"+ DBUtils.formatToTwoSigns(totalPrice));
    }

    private void sellProducts() throws SQLException, InterruptedException {
        if(!DBUtils.makeTransaction(lvProducts,DBUtils.getUserName())) {
            DBUtils.showErrorMessage("The transaction could not be completed! ");
        }
        reverseProduct=0;
        lvProducts.getItems().clear();
        lbPrice.setText("Price:");
    }
}
