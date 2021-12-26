package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignInController{


    public AnchorPane anchorRoot;
    public Text SignUp;

    public void ShowSignUp(MouseEvent mouseEvent) throws IOException {

        URL url = new File("src/main/resources/FXML/SignUp.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) SignUp.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    private void initialize()
    {
    }

    public void SignInController(){}
}