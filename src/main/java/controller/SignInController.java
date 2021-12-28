package main.java.controller;

import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import main.java.connection.ConnectionMongoDB;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignInController {

    public static Text Home;
    public AnchorPane anchorRoot;
    public Text SignUp;
    @FXML private TextField us;
    @FXML private TextField pw;

    public void ShowSignUp(MouseEvent mouseEvent) throws IOException {

        URL url = new File("src/main/resources/FXML/SignUp.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) SignUp.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    private void initialize() {

    }

    public static void ShowHome() throws IOException {

        URL url = new File("src/main/resources/FXML/Home.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) Home.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    public void login(ActionEvent actionEvent) throws IOException {

        if(!us.getText().isEmpty() && !pw.getText().isEmpty()) {
            System.out.println("Value: " + us.getText());
            System.out.println(("Value: " + pw.getText()));

            ConnectionMongoDB conn = new ConnectionMongoDB();
            boolean logged = conn.logInUser(us.getText(), pw.getText());
            if(logged)
                ShowHome();

            //clear TextField
            us.setText("");
            pw.setText("");
        }

    }
}