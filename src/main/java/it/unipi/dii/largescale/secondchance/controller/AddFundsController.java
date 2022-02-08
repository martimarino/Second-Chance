package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Balance;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import sun.java2d.cmm.Profile;

import java.io.IOException;
import java.util.Objects;

public class AddFundsController {

    private User user;

    @FXML private Button btnDeposit;

    @FXML private TextField txtFieldCode;

    public void initialize(){

        user  = Session.getLoggedUser();

        btnDeposit.setDisable(true);

        txtFieldCode.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            btnDeposit.setDisable(Objects.equals(newValue, ""));
        });
    }

    public void deposit() {

        String code = txtFieldCode.getText();

        Utility.printTerminal("CREDIT (before): " + Balance.balance.getCredit());
        double newCredit = ConnectionMongoDB.connMongo.addFundsToWallet(user.getUsername(), code);
        Utility.printTerminal("CREDIT (after): " + Balance.balance.getCredit());

        txtFieldCode.setText("");

        Stage stage = (Stage) btnDeposit.getScene().getWindow();
        stage.close();

    }
}
