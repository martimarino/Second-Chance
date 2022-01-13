package main.java.controller;


import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.*;

import main.java.connection.*;
import main.java.entity.*;
import main.java.utils.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SignUpController {

    public Text SignIn;
    @FXML private TextField us, pw, em, nm, co, ci, ad;

    public void ShowSignIn() throws IOException {

        URL url = new File("src/main/resources/FXML/SignIn.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) SignIn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private void initialize() {

    }

    public void registration() throws IOException {

        if (!us.getText().isEmpty()
                && !pw.getText().isEmpty() && !em.getText().isEmpty()
                && !nm.getText().isEmpty() && !ci.getText().isEmpty() && !co.getText().isEmpty()
                && !ad.getText().isEmpty()) {

            User u = new User(em.getText(), us.getText(), pw.getText(), nm.getText(), co.getText(), ci.getText(), ad.getText(), "N", 0.0, 0, new ArrayList<Order>());

            if(us.getText().equals("admin")) {
                Utility.infoBox("You can not register as admin", "Error", "Please, insert a different username-");
                us.setText("");
                return;
            }

            System.out.println(u.toString());

            ConnectionMongoDB conn = new ConnectionMongoDB();

            if(conn.registerUser(u)) {
                //clear TextField
                us.setText("");
                pw.setText("");
                em.setText("");
                nm.setText("");
                ci.setText("");
                co.setText("");
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
