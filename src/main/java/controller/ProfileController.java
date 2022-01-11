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

    public void initialize(){

        Session session = Session.getInstance();
        User user  = session.getLogUser();

        Label username = new Label(user.getUsername());
        Label name = new Label(user.getName());
        Label email = new Label(user.getEmail());
        Label country = new Label(user.getCountry());
        Label city = new Label(user.getCity());
        Label address = new Label(user.getAddress());
        System.out.println(username + " " + name +  " " + email +  " " + country +  " " + city +  " " + address);
        userInfo.add(username, 1,0);
        userInfo.add(name, 1, 1);
        userInfo.add(email, 1, 2);
        userInfo.add(country, 1, 3);
        userInfo.add(city, 1, 4);
        userInfo.add(address, 1, 5);
    }

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
