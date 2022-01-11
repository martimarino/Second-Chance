package main.java.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainController {

    public AnchorPane anchorRoot;

    public TabPane tabpane;
    @FXML
    private HomeController homeController;

    @FXML
    private MyProfileController myProfile;

    @FXML
    private SearchUserController searchUser;

    @FXML
    private SearchInsertionController searchInsertion;


}
