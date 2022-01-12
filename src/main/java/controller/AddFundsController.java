package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;

public class AddFundsController {

    @FXML private Button btnAddFunds;
    @FXML private RadioButton radioBtn10;
    @FXML private RadioButton radioBtn25;
    @FXML private RadioButton radioBtn50;
    @FXML private RadioButton radioBtn100;

    public void initialize(){
       btnAddFunds.setDisable(true);
    }

    public void activeRequestBtn() {
        if (radioBtn10.isSelected() || radioBtn25.isSelected() ||
                radioBtn50.isSelected() || radioBtn100.isSelected()) {
            btnAddFunds.setDisable(false);
        }
    }

    public void addFunds() {

        int amount = 0;

        if (radioBtn10.isSelected())
            amount = 10;
        if (radioBtn10.isSelected())
            amount = 25;
        if (radioBtn10.isSelected())
            amount = 50;
        if (radioBtn10.isSelected())
            amount = 100;

        // manca campo "Saldo" all'interno della collection User


    }
}
