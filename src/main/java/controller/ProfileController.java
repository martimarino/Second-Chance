package main.java.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.java.entity.User;
import main.java.utils.Session;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ProfileController{
    public Button feed;
    public Button followersButton;
    public Button followingButton;
    public Button interestedInsertionsButton;
    public GridPane userInfo;
    private String user;

    public void initialize(){}

    public void showUserFollowers(MouseEvent mouseEvent) {
    }

    public void showUserFollowing(MouseEvent mouseEvent) {
    }

    public void showInterestedInsertions(MouseEvent mouseEvent) {
    }

    public void setUser(String username) {
        this.user = username;
        System.out.println(this.user);
    }
}
