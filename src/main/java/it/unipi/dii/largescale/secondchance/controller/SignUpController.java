package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.CryptWithMD5;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;

import java.io.IOException;

public class SignUpController {

    public Text SignIn;
    @FXML private TextField us, pw, em, nm, ci, ad, image;
    @FXML private ComboBox<String> co;

    public void ShowSignIn() throws IOException {

        Stage stage = (Stage) SignIn.getScene().getWindow();
        Utility.changePage(stage, "SignIn");

    }

    public void registration() throws IOException {

        String img;

        if (!us.getText().isEmpty()
                && !pw.getText().isEmpty() && !em.getText().isEmpty()
                && !nm.getText().isEmpty() && !ci.getText().isEmpty() && !co.getValue().isEmpty()
                && !ad.getText().isEmpty()) {

            if(image.getText().equals(""))
                img = "user.png";
            else
                img = image.getText();
            String encrypted = CryptWithMD5.cryptWithMD5(pw.getText());
            User u = new User(em.getText(), us.getText(),
                    encrypted, nm.getText(), co.getValue(),
                    ci.getText(), ad.getText(), false,
                    Double.NaN, 0.0, img, null,
                    null, null);

            if(us.getText().equals("admin")) {
                Utility.infoBox("You can not register as admin", "Error", "Please, insert a different username-");
                us.setText("");
                return;
            }

            System.out.println(u);

            if(ConnectionMongoDB.connMongo.registerUser(u)) {
                //clear TextField
                us.setText("");
                pw.setText("");
                em.setText("");
                nm.setText("");
                ci.setText("");
                co.setValue("Select your country");
                ad.setText("");
                image.setText("");

                if(!ConnectionNeo4jDB.connNeo.addUser(u))
                {
                    Utility.printTerminal("Error registration user");
                    Utility.infoBox("Error adding new user" , "Error", "Error adding new user");
                    ConnectionMongoDB.connMongo.deleteUserMongo(u.getUsername());
                    return;
                }
                ShowSignIn();
                Utility.infoBox("Now you can login!", "Confirmed", "Registration completed with success!");

            }

        }else {
            Utility.infoBox("Please, fill all information.", "Error", "Empty fields!");
        }
    }
}
