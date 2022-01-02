package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.java.connection.ConnectionMongoDB;
import main.java.utils.Session;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignInController {

    public AnchorPane anchorRoot;
    public Button SignUp;
    public Button SignIn;
    @FXML private TextField us;
    @FXML private PasswordField pw;

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

    public void ShowAdminPanel() throws IOException {

        URL url = new File("src/main/resources/FXML/AdminPanel.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) SignIn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    public void ShowHome() throws IOException {

        URL url = new File("src/main/resources/FXML/Home.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) SignIn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    public void login(ActionEvent actionEvent) throws IOException {

        String username = us.getText();
        String password = pw.getText();
        Session session;

        if(!us.getText().isEmpty() && !pw.getText().isEmpty()) {
            System.out.println("Value: " + us.getText());
            System.out.println(("Value: " + pw.getText()));

            ConnectionMongoDB conn = new ConnectionMongoDB();

            if(us.getText().equals("admin") && pw.getText().equals("admin")) {
                session = Session.getInstance();
                session.setLogUser(username);
                ShowAdminPanel();
            } else {
                boolean logged = conn.logInUser(us.getText(), pw.getText());
                if (logged) {
                    session = Session.getInstance();
                    session.setLogUser(username);
                    ShowHome();
                    }
                }
            //clear TextField
            us.setText("");
            pw.setText("");
        }

    }
}