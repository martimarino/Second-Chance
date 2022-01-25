package main.java.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;

public class CodesController {

    @FXML private Text txtResult;

    public void generateCodes() throws IOException {

        Process p = Runtime.getRuntime().exec("python randomCodesGenerator.py");

        txtResult.setText("Codes generated successfully!");

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        txtResult.setText("");
                    }
                },
                5000
        );
    }

}
