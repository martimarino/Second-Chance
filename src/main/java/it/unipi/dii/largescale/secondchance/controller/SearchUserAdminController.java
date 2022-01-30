package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.IOException;
import java.util.Objects;

public class SearchUserAdminController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;

    @FXML private Text email;
    @FXML private Text country;
    @FXML private Text city;
    @FXML private Text address;
    @FXML private Text alertText;
    @FXML private Text txtResult;

    @FXML private Button btnLogout;
    @FXML private Button btnSuspendUsr;
    @FXML private Button btnUnsuspendUsr;

    private String username;

    private Session session;

    public void initialize(){

        btnSuspendUsr.setDisable(true);
        btnUnsuspendUsr.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            if (!Objects.equals(newValue, ""))
                usernameField.setText("");
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            if (!Objects.equals(newValue, ""))
                nameField.setText("");
        });
    }

    public void searchUser(){

        Document found;

        username = usernameField.getText();
        String name = nameField.getText();

        System.out.println("username: " + username);
        System.out.println("name: " + name);

        if(name != null && !name.trim().isEmpty()) {

            found = ConnectionMongoDB.connMongo.verifyUserInDB(name, false);

            if (found == null) {
                Utility.infoBox("The user is not present in the system. Please try again.",
                        "Error!",
                        "User not found!");
                return;
            }

            username = found.getString("username");
        }else {
            found = ConnectionMongoDB.connMongo.verifyUserInDB(username, true);
        }

        if (found == null || ((name == null && name.trim().isEmpty()) &&
                        (username != null && username.trim().isEmpty()))) {
            Utility.infoBox("The user is not present in the system. Please try again.",
                            "Error!",
                            "User not found!");
        }else {

            User usr = ConnectionMongoDB.connMongo.findUserDetails(username);
            //System.out.println("Suspended: " + usr.getSuspended());

            if (Objects.equals(usr.getSuspended(), "Y")) {
                //System.out.println("Suspended!");
                btnSuspendUsr.setDisable(true);
                btnUnsuspendUsr.setDisable(false);
            }else{
                //System.out.println("Unsuspended!");
                btnSuspendUsr.setDisable(false);
                btnUnsuspendUsr.setDisable(true);
            }

            email.setText(usr.getEmail());
            country.setText(usr.getCountry());
            city.setText(usr.getCity());
            address.setText(usr.getAddress());
        }
    }

    public void suspendUser() {

        ConnectionMongoDB.connMongo.suspendUser(username);

        alertText.setText("User suspended!");

        btnSuspendUsr.setDisable(true);
        btnUnsuspendUsr.setDisable(false);
    }

    public void unsuspendUser() {

        ConnectionMongoDB.connMongo.unsuspendUser(username);

        alertText.setText("User unsuspended!");

        btnSuspendUsr.setDisable(false);
        btnUnsuspendUsr.setDisable(true);
    }

    public void logout() throws IOException {

        session = Session.getInstance();
        session.getLogoutUser();

        // Closing current window
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();

        // Open sign-in window
        Stage primaryStage = new Stage();
        primaryStage.setTitle("SecondChance");
        Utility.changePage(primaryStage, "SignIn");
    }

    public void generateCodes() throws IOException {

        Process p = Runtime.getRuntime().exec("python randomCodesGenerator.py");

        txtResult.setText("  Codes generated successfully! ");

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        txtResult.setText("Here you can generated new codes!");
                    }
                },
                5000
        );
    }
}
