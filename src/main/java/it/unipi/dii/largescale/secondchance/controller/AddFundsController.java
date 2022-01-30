package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;

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
