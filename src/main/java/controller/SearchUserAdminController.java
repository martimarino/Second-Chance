package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import main.java.entity.User;
import main.java.connection.ConnectionMongoDB;
import main.java.utils.Utility;
import org.bson.Document;

import java.util.Objects;

public class SearchUserAdminController {

    @FXML private TextField nameField;
    @FXML private Text email;
    @FXML private Text country;
    @FXML private Text city;
    @FXML private Text address;
    @FXML private Text alertText;
    @FXML private Button btnDeleteUsr;
    @FXML private Button btnSuspendUsr;
    @FXML private Button btnUnsuspendUsr;

    private String username;

    public void initialize(){
        btnDeleteUsr.setDisable(true);
        btnSuspendUsr.setDisable(true);
        btnUnsuspendUsr.setDisable(true);
    }

    public void searchUser(){

        ConnectionMongoDB conn = new ConnectionMongoDB();

        Document found;
        String name = nameField.getText();

        if(name != null && !name.trim().isEmpty()) {
            found = conn.verifyUserInDB(name, false);
            username = found.getString("username");
        }else {
            found = conn.verifyUserInDB(username, true);
        }

        if (found == null ||   ((name == null && name.trim().isEmpty()) &&
                        (username != null && username.trim().isEmpty()))) {
            Utility.infoBox("The user is not present in the system. Please try again.",
                            "Error!",
                            "User not found!");
        } else {

            User usr = conn.findUserDetails(username);
            System.out.println("Suspended: " + usr.getSuspended());

            if (Objects.equals(usr.getSuspended(), "Y")) {
                System.out.println("Suspended!");
                btnSuspendUsr.setDisable(true);
                btnUnsuspendUsr.setDisable(false);
            }else{
                System.out.println("Unsuspended!");
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

        ConnectionMongoDB conn = new ConnectionMongoDB();
        conn.suspendUser(username);

        alertText.setText("User suspended!");

        btnDeleteUsr.setDisable(true);
        btnSuspendUsr.setDisable(true);
        btnUnsuspendUsr.setDisable(false);

    }

    public void unsuspendUser() {

        ConnectionMongoDB conn = new ConnectionMongoDB();
        conn.unsuspendUser(username);

        alertText.setText("User unsuspended!");

        btnDeleteUsr.setDisable(false);
        btnSuspendUsr.setDisable(false);
        btnUnsuspendUsr.setDisable(true);

    }





}
