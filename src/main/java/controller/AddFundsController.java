package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.java.connection.ConnectionMongoDB;
import main.java.entity.User;
import main.java.utils.Session;

import java.util.Objects;

public class AddFundsController {

    private User user;

    @FXML private Button btnDeposit;

    @FXML private TextField txtFieldCode;

    public void initialize(){

        user  = Session.getLogUser();

        btnDeposit.setDisable(true);

        txtFieldCode.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            btnDeposit.setDisable(Objects.equals(newValue, ""));
        });
    }

    public void deposit() {

        String code = txtFieldCode.getText();

        ConnectionMongoDB.connMongo.addFundsToWallet(user.getUsername(), code);

        txtFieldCode.setText("");

    }
}
