package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.connection.ConnectionMongoDB;
import main.java.entity.User;
import main.java.utils.Session;
import main.java.utils.Utility;

import java.io.IOException;
import java.util.Objects;

public class AddFundsController {

    private User user;

    @FXML private Button btnDeposit;

    @FXML private TextField txtFieldCode;

    public void initialize(){
        Session session = Session.getInstance();
        user  = session.getLogUser();

        btnDeposit.setDisable(true);

        txtFieldCode.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            btnDeposit.setDisable(Objects.equals(newValue, ""));
        });
    }

    public void deposit() throws IOException {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        String code = txtFieldCode.getText();

        conn.addFundsToWallet(user.getUsername(), code);

        txtFieldCode.setText("");

    }
}
