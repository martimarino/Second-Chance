package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Balance;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;

import java.util.Objects;

public class AddFundsController {

    @FXML private Button btnDeposit;
    @FXML private TextField txtFieldCode;

    public void initialize(){

        btnDeposit.setDisable(true);

        txtFieldCode.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            btnDeposit.setDisable(Objects.equals(newValue, ""));
        });
    }

    public void deposit() {

        String code = txtFieldCode.getText();
        ConnectionMongoDB.connMongo.addFundsToWallet(code);

        txtFieldCode.setText("");

        Stage stage = (Stage) btnDeposit.getScene().getWindow();
        stage.close();

    }
}
