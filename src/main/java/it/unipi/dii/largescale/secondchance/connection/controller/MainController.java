package main.java.it.unipi.dii.largescale.secondchance.connection.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class MainController {

    public AnchorPane anchorRoot;

    public TabPane tabpane;

    @FXML
    private HomeController homeController;

    @FXML
    private ProfileController myProfile;

    @FXML
    private SearchUserController searchUser;

    @FXML
    private SearchInsertionController searchInsertion;

}
