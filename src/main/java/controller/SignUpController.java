package main.java.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.connection.ConnectionMongoDB;
import main.java.connection.ConnectionNeo4jDB;
import main.java.entity.User;
import main.java.utils.CryptWithMD5;
import main.java.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignUpController {

    public Text SignIn;
    @FXML private TextField us, pw, em, nm, ci, ad;
    @FXML
    private ComboBox<String> co;

    public void ShowSignIn() throws IOException {

        Stage stage = (Stage) SignIn.getScene().getWindow();
        Utility.changePage(stage, "SignIn");

    }

    public void registration() throws IOException {

        if (!us.getText().isEmpty()
                && !pw.getText().isEmpty() && !em.getText().isEmpty()
                && !nm.getText().isEmpty() && !ci.getText().isEmpty() && !co.getValue().isEmpty()
                && !ad.getText().isEmpty()) {

            String encrypted = CryptWithMD5.cryptWithMD5(pw.getText());
            User u = new User(em.getText(), us.getText(), encrypted, nm.getText(), co.getValue(), ci.getText(), ad.getText(), "N", Double.NaN, 0.0, "image.png");

            if(us.getText().equals("admin")) {
                Utility.infoBox("You can not register as admin", "Error", "Please, insert a different username-");
                us.setText("");
                return;
            }

            System.out.println(u);

            ConnectionMongoDB conn = new ConnectionMongoDB();

            if(conn.registerUser(u)) {
                //clear TextField
                us.setText("");
                pw.setText("");
                em.setText("");
                nm.setText("");
                ci.setText("");
                co.setValue("Select your country");
                ad.setText("");

                ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();
                connNeo.addUser(u);
                ShowSignIn();
                Utility.infoBox("Now you can login!", "Confirmed", "Registration completed with success!");

            }

        }else {
            Utility.infoBox("Please, fill all information.", "Error", "Empty fields!");
        }
    }
}
