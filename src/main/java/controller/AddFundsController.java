package main.java.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import main.java.connection.ConnectionMongoDB;
import main.java.entity.User;
import main.java.utils.Session;

public class AddFundsController {

    private User user;

    @FXML private Button btnDeposit;
    @FXML private RadioButton radioBtn10;
    @FXML private RadioButton radioBtn20;
    @FXML private RadioButton radioBtn25;
    @FXML private RadioButton radioBtn50;
    @FXML private RadioButton radioBtn100;
    @FXML private RadioButton radioBtn200;

    @FXML private TextField txtFieldCode;

    public void initialize(){
        Session session = Session.getInstance();
        user  = session.getLogUser();

        btnDeposit.setDisable(true);
    }

    public void activeRequestBtn() {
        if (radioBtn10.isSelected() || radioBtn20.isSelected() ||
                radioBtn25.isSelected() || radioBtn50.isSelected() ||
                radioBtn100.isSelected() || radioBtn200.isSelected())
            btnDeposit.setDisable(false);
    }

    public void deposit() {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        double credit = 0;

        if (radioBtn10.isSelected())
            credit = 10;
        if (radioBtn20.isSelected())
            credit = 20;
        if (radioBtn25.isSelected())
            credit = 25;
        if (radioBtn50.isSelected())
            credit = 50;
        if (radioBtn100.isSelected())
            credit = 100;
        if (radioBtn200.isSelected())
            credit = 200;

        String code = txtFieldCode.getText();

        conn.addFundsToWallet(user.getUsername(), credit, code);
    }
}
