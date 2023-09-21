package com.example.loginform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Stack;

public class ProductReturn extends SuccessLogin implements Initializable {

    @FXML
    private Button btnDeleteItem;

    @FXML
    private Button btnFinish;

    @FXML
    private Button btnSearchBill;

    @FXML
    private ListView<Product> lvReturnedItem;

    @FXML
    private TextField tfSearchDate;

    @FXML
    private TextField tfSearchMinutes;

    @FXML
    private Button btnClearListView;

    private static Stack<Product> productStack=new Stack<>();

    private Product product;
    @Override
    protected void deleteItemFromListView(ListView listView){
        int selectedIndex= listView.getSelectionModel().getSelectedIndex();
        if(selectedIndex>=0){
            product = (Product) listView.getItems().get(selectedIndex);
            productStack.add(product);
            listView.getItems().remove(selectedIndex);
        }
    }

    private boolean updateDeletedProducuts(Stack<Product> productStack) throws SQLException {
        Iterator<Product> productIterator=productStack.iterator();

        while(productIterator.hasNext()){
            Product product1=productIterator.next();
            DBUtils.removeProductFromBill(product1, tfSearchDate.getText(),tfSearchMinutes.getText());
            System.out.println(product1);
        }
        productStack.clear();
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSearchBill.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    for(Product product:DBUtils.searchBill(tfSearchDate.getText().trim(),tfSearchMinutes.getText().trim())){
                        lvReturnedItem.getItems().add(product);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnDeleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteItemFromListView(lvReturnedItem);
            }
        });

        btnFinish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    updateDeletedProducuts(productStack);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnClearListView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                lvReturnedItem.getItems().clear();
                productStack.clear();
            }
        });
    }

}


