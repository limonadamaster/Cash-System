package com.example.loginform;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import java.io.FileWriter;
import java.io.IOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    //Sum the total price for cashier
    private static Double cashForCashier=0.0;
    public static void setCashForCashier(Double cashForCashier) { cashForCashier = cashForCashier;}
    public static Double getCashForCashier() {
        return cashForCashier;
    }
    private static boolean isCashierLogged = false;
    //get cashier name/number
    private static String userName ;
    public static String getUserName() {
        return userName;
    }
    public static void setUserName(String userName) {
        DBUtils.userName = userName;
    }

    private final static String selectUsersUsername="SELECT * FROM Users WHERE Username = ?";

    private final static String selectFromProducts="SELECT * FROM products WITH(NOLOCK)";

    private final static String updateProductsSalesWherePLU="UPDATE products WITH(UPDLOCK) SET sales = sales+1 WHERE PLU_identificator = ?";

    private final static String insertUsersUsernamePassword="INSERT INTO Users (Username,Password) VALUES(?,?)";

    private final static String insertBill = "INSERT INTO Bills (BillData,BillMinutes) VALUES(?,?);";

    private final static String urlSQL="jdbc:sqlserver://CECKO\\SQLEXPRESS;Database=Phonebook;IntegratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private final static String columnForSellPrice="price";

    private final static String columnForSelledPrice="PriceAtTimeOfSale";
    
    private static String[] holdDateMinutes = new String[2];
    public static void changeScene(ActionEvent event,Stage previousStage, String fxmlFile, String title, String username){
        Parent root = null;

        if(username!=null){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = fxmlLoader.load();
                SuccessLogin successLogin = fxmlLoader.getController();
                   successLogin.setUsername(username);

            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            try{
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
        } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root, 600, 400));
        stage.initStyle(StageStyle.TRANSPARENT); // Set the style before showing the stage

        if(previousStage!=null){
            previousStage.close();
        }
        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode()== KeyCode.ESCAPE){
                    stage.close();
                }
            }
        });
        stage.show();
}
public static void signUpUser(ActionEvent actionEvent,String username,String password) {
    Connection connection = null;
    PreparedStatement psInsert = null;
    PreparedStatement psCheckUserExists = null;
    ResultSet resultSet = null;

    try {
        connection = DriverManager.getConnection(urlSQL);
        psCheckUserExists = connection.prepareStatement("SELECT * FROM Users WITH(NOLOCK) WHERE Username = ?");
        psCheckUserExists.setString(1, username);
        resultSet = psCheckUserExists.executeQuery();

        if (resultSet.isBeforeFirst()) {
            System.out.println("User exists");
            showErrorMessage("You cannot use this username!");
        } else {
            psInsert = connection.prepareStatement(insertUsersUsernamePassword);
            psInsert.setString(1, username);
            psInsert.setString(2, Encryptor.encryptString(password));
            psInsert.executeQuery();

            changeScene(actionEvent,SignUp.getStageSignUp(), "success-login.fxml", "Welcome", username);
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    } finally {
        closeDatabaseConnection(connection,psInsert,psCheckUserExists,resultSet);
    }
}
public static boolean deleteReturnedItemFromBill(Product product) throws SQLException {
    Connection connection = null;
    PreparedStatement psCheckUserExists = null;

    connection = DriverManager.getConnection(urlSQL);

        return true;
}
public static boolean makeTransaction(ListView<Product> listView,String usernameTxt) throws SQLException {
    Connection connection = null;
    PreparedStatement psCheckUserExists = null;
    ResultSet generatedKeys = null;
    Double totalPrice=0.0;
    long generatedID =0;

    try {
        connection = DriverManager.getConnection(urlSQL);
           if(listView.getItems().isEmpty()){
               return false;
           }
            FileWriter fileWriter=new FileWriter(usernameTxt+".txt",true);
            fileWriter.write("------Begin bill------- ");
            fileWriter.write(setTimeNow());

            //if boolean for sell
            connection.setAutoCommit(false);

            holdDateMinutes =DBUtils.setTimeNow().trim().split(" ");
        psCheckUserExists = connection.prepareStatement(insertBill,Statement.RETURN_GENERATED_KEYS);
        psCheckUserExists.setString(1,holdDateMinutes[0]);
        psCheckUserExists.setString(2,holdDateMinutes[1]);
        psCheckUserExists.executeUpdate();

        generatedID=getGeneratedKey(generatedKeys,psCheckUserExists);
            if(generatedID==0) {
                showErrorMessage("Cannot find ID for Bill!");
                return false;
            }

        for(Product product : listView.getItems()) {

            insertProductsToBills(connection,psCheckUserExists,product,generatedID);

            psCheckUserExists = connection.prepareStatement(updateProductsSalesWherePLU);
            psCheckUserExists.setString(1, product.getPLU());

            fileWriter.write( product.getName()+"   "+product.getPrice()+"\n");
            totalPrice+=Double.parseDouble(product.getPrice());

            psCheckUserExists.executeUpdate();
        }
        cashForCashier+=totalPrice;
        fileWriter.write("Total price: "+formatToTwoSigns(totalPrice)+"\n------End bill------- ");
        fileWriter.write(setTimeNow()+"\n\n");
        fileWriter.close();

        connection.commit();
        showErrorMessage("Transaction was successful!");
    } catch (SQLException e) {
       if(connection!=null)
             connection.rollback();
        e.printStackTrace();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    finally {
        closeDatabaseConnection(connection,psCheckUserExists,null,generatedKeys);
        return true;
    }
}
public static Product searchProduct(String getProductPlu) throws SQLException {
    Connection connection = null;
    PreparedStatement psInsert = null;
    PreparedStatement psCheckUserExists = null;
    ResultSet resultSet = null;
    try {
        connection = DriverManager.getConnection(urlSQL);
        psCheckUserExists = connection.prepareStatement(selectFromProducts+" WHERE PLU_identificator = ?");
        psCheckUserExists.setString(1, getProductPlu);
        resultSet = psCheckUserExists.executeQuery();

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Product not found");
            showErrorMessage("Product not found!");
        } else {

            while (resultSet.next()) {
                String pluIdentificator = resultSet.getString("PLU_identificator");
                if (pluIdentificator.equals(getProductPlu)) {
                    return setProductFields(resultSet,columnForSellPrice);
                }
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } finally {
        closeDatabaseConnection(connection, psInsert, psCheckUserExists, resultSet);
    }
   return null;
}

public static void logInUser(ActionEvent actionEvent,String username,String password) throws SQLException {
    Connection connection = null;
    PreparedStatement psInsert = null;
    PreparedStatement psCheckUserExists = null;
    ResultSet resultSet = null;

    try {
        connection = DriverManager.getConnection(urlSQL);
        psCheckUserExists = connection.prepareStatement(selectUsersUsername);
        psCheckUserExists.setString(1, username);
        resultSet = psCheckUserExists.executeQuery();

        if(!resultSet.isBeforeFirst()){
            System.out.println("User not found in database");
            showErrorMessage("User not found!");
        }else{
            while(resultSet.next()){
                String retrievedPassword = resultSet.getString("Password");
                if(Encryptor.checkPassword(password,retrievedPassword)){
                    changeScene(actionEvent,LoginForm.getStageLoginForm(),"success-login.fxml","Welcome",username);

                    System.out.println("Password  match");
                    DBUtils.setLogInLogOutInformationToFile(username+".txt"," logged in ");
                    userName = username;
                }else{
                    System.out.println("Password don't match");
                    DBUtils.showErrorMessage("Password don't match!");
                }
            }
        }
    } catch (SQLException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    } finally {
        closeDatabaseConnection(connection,psInsert,psCheckUserExists,resultSet);
    }
}
    public static void setLogInLogOutInformationToFile(String userNameToFile,String LoginLogOutText){
        try{
            if(!isCashierLogged)
                isCashierLogged=true;
            else
                isCashierLogged=false;

            FileWriter writer = new FileWriter(userNameToFile,true);

            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            String formattedDate = myDateObj.format(myFormatObj);

            writer.write("Cashier "+userNameToFile.substring(0,2)+ LoginLogOutText +  formattedDate+ "\n\n");
            if(!isCashierLogged) {
                writer.write("Money from cashier " + userName + " shift :" + formatToTwoSigns(cashForCashier)+"\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Product> loadProducts() {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        List<Product> productList = null;
        try {
            connection = DriverManager.getConnection(urlSQL);
            psCheckUserExists = connection.prepareStatement(selectFromProducts);
            resultSet = psCheckUserExists.executeQuery();

            productList = new ArrayList<>();
            while (resultSet.next()) {
                productList.add(setProductFields(resultSet,columnForSelledPrice));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeDatabaseConnection(connection,psInsert,psCheckUserExists,resultSet);
            return productList;
        }
    }
        private static String setTimeNow(){
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj) +"\n";
        return formattedDate;
    }

    public static String formatToTwoSigns(Double numberForFormat){
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedValue = df.format(numberForFormat);
        return formattedValue;
    }
    public static void showErrorMessage(String specificErrorText)   {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(specificErrorText);
        alert.show();

    }
    public static void closeDatabaseConnection(Connection connection,PreparedStatement psInsert,
                                               PreparedStatement psCheckUserExists,ResultSet resultSet){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psCheckUserExists != null) {
            try {
                psCheckUserExists.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (psInsert != null) {
            try {
                psInsert.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final long getGeneratedKey(ResultSet generatedKeys, PreparedStatement psCheckUserExists) throws SQLException {
        generatedKeys= psCheckUserExists.getGeneratedKeys();
        if(generatedKeys.next()){
            return generatedKeys.getInt(1);
        }
        return 0;
    }

    private  static void insertProductsToBills(Connection connection,PreparedStatement psCheckUserExists,Product product,long generatedID) throws SQLException {

        String sql = "MERGE INTO BILL_PRODUCT AS Target " +
                "USING (VALUES ("+generatedID+",(SELECT product_id  FROM Products WITH(NOLOCK) WHERE PLU_identificator= "+product.getPLU()+"),(SELECT price FROM Products WITH(NOLOCK) WHERE PLU_identificator = "+product.getPLU()+"))) AS Source (BillID, ProductID, PriceAtTimeOfSale) " +
                "ON Target.BillID = Source.BillID AND Target.ProductID = Source.ProductID " +
                "WHEN MATCHED THEN " +
                "    UPDATE SET Quantity = Quantity + 1 " +
                "WHEN NOT MATCHED THEN " +
                "    INSERT (BillID, ProductID, PriceAtTimeOfSale, Quantity) " +
                "    VALUES (Source.BillID, Source.ProductID, Source.PriceAtTimeOfSale, 1);";
        psCheckUserExists=connection.prepareStatement(sql);

        psCheckUserExists.addBatch();
        psCheckUserExists.executeBatch();
    }

    public static List<Product> searchBill(String dateOfSell,String minutesOfSell) throws SQLException {
        Connection connection = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
int quantity=0;
        connection = DriverManager.getConnection(urlSQL);
        List<Product> productList = null;
        try {


            psCheckUserExists = connection.prepareStatement("SELECT B.BillID, B.BillData, P.product_name, BP.PriceAtTimeOfSale,P.PLU_identificator,BP.Quantity\n" +
                    "FROM Bills B\n" +
                    "JOIN BILL_PRODUCT BP ON B.BillID = BP.BillID\n" +
                    "JOIN Products P ON BP.ProductID = P.product_id\n" +
                    "WHERE B.BillMinutes LIKE '%"+minutesOfSell+"%' AND B.BillData = '"+dateOfSell+"';");

            resultSet = psCheckUserExists.executeQuery();

            productList = new ArrayList<>();
            while (resultSet.next()) {
                quantity=resultSet.getInt("Quantity");
                while(quantity>0){
                    productList.add(setProductFields(resultSet,"PriceAtTimeOfSale"));
                    quantity--;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDatabaseConnection(connection, null, psCheckUserExists, resultSet);
            return productList;
        }
    }

    public static Product setProductFields(ResultSet resultSet,String WorkflowOfSale) throws SQLException {
        Product product = new Product();
        product.setName(resultSet.getString("product_name"));
        product.setPrice(resultSet.getString(WorkflowOfSale));
        product.setPLU(resultSet.getString("PLU_identificator"));
        return product;
    }

    public static int removeProductFromBill(Product product,String dateOfBill,String minutesOfBill) throws SQLException {

        Connection connection = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        int quantity=0;
        connection = DriverManager.getConnection(urlSQL);
        connection.setAutoCommit(false);
        try {
            //decrease

            psCheckUserExists = connection.prepareStatement("SELECT BP.Quantity\n" +
                    "FROM Bills B\n" +
                    "JOIN BILL_PRODUCT BP ON B.BillID = BP.BillID\n" +
                    "JOIN Products P ON BP.ProductID = P.product_id\n" +
                    "WHERE B.BillMinutes LIKE '%"+minutesOfBill+"%' AND B.BillData = '"+dateOfBill+"'" +
                    "AND P.product_id =(SELECT product_id FROM Products WHERE PLU_identificator = "+product.getPLU()+")");

            resultSet = psCheckUserExists.executeQuery();

            while (resultSet.next()){
                quantity=resultSet.getInt("Quantity");
            }

            if(quantity>1) {
                psCheckUserExists = connection.prepareStatement("UPDATE BILL_PRODUCT\n" +
                        "SET Quantity = Quantity - 1\n" +
                        "FROM BILL_PRODUCT\n" +
                        "JOIN Bills B ON B.BillID = BILL_PRODUCT.BillID\n" +
                        "JOIN Products P ON BILL_PRODUCT.ProductID = P.product_id\n" +
                        "WHERE B.BillMinutes LIKE '%" + minutesOfBill + "%' AND B.BillData = '" + dateOfBill + "'\n" +
                        "  AND Quantity > 1\n" +
                        "AND P.product_id = (SELECT product_id FROM ProdUcts WHERE PLU_identificator =" + product.getPLU() + ");" +
                        "\n");
            }
            else if (quantity==1) {
                //delete
                psCheckUserExists = connection.prepareStatement("DELETE FROM BILL_PRODUCT\n" +
                        "WHERE Quantity = 1\n" +
                        "  AND EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM Bills B\n" +
                        "    WHERE B.BillID = BILL_PRODUCT.BillID\n" +
                        "    AND B.BillMinutes LIKE '%" + minutesOfBill + "%' \n" +
                        "    AND B.BillData = '" + dateOfBill + "'\n" +
                        "  )\n" +
                        "  AND ProductID = (SELECT product_id FROM ProdUcts WHERE PLU_identificator =" + product.getPLU() + ");");
            }

            psCheckUserExists.executeUpdate();

            connection.commit();

    }catch (SQLException E){
            E.printStackTrace();
            connection.rollback();
        }
        return quantity;
    }
}
