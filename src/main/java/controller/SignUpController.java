package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.connection.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignUpController {

    public AnchorPane anchorRoot;
    public Text SignIn;
    @FXML private TextField us;
    @FXML private TextField pw;


    public void ShowSignIn(MouseEvent mouseEvent) throws IOException {

        URL url = new File("src/main/resources/FXML/SignIn.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) SignIn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    private void initialize() {

    }

}