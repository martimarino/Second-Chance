package main.java.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ProfileController {
    public Button feed;
    public Button followersButton;
    public Button followingButton;
    public Button interestedInsertionsButton;

    public void showHome(MouseEvent mouseEvent) throws IOException {
        URL url = new File("src/main/resources/FXML/Home.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) feed.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public void showUserFollowers(MouseEvent mouseEvent) {
    }

    public void showUserFollowing(MouseEvent mouseEvent) {
    }

    public void showInterestedInsertions(MouseEvent mouseEvent) {
    }
}
