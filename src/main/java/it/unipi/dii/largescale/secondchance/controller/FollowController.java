package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;

public class FollowController {

    public ArrayList<String> list;
    public BorderPane bp;
    public VBox box;
    public Pane prev, next;

    private final int nPage = 8;
    private int scrollPage;

    String type_img;
    User userLogged;

    public void initialize(ArrayList<String> users){

        userLogged = Session.getLoggedUser();
        type_img = "user";
        list = users;
        scrollPage = 0;
        box = new VBox(20);

        prev.setVisible(false);
        prev.setDisable(true);

        show();
    }

    public void show() {

        box.getChildren().clear();

        // If there are more than k insertions enable next button
        if (list.size() < nPage) {
            next.setDisable(true);
            next.setVisible(false);
        }

        for (int i = 0; i < nPage && scrollPage < list.size(); i++)
            addUser();

        bp.setCenter(box);
    }

    private void addUser() {

        String user = list.get(scrollPage);
        HBox hb = new HBox();
        Label username = new Label(list.get(scrollPage));
        Button follow = new Button();

        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);

        username.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        follow.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

        if (ConnectionNeo4jDB.connNeo.checkIfFollows(userLogged.getUsername(), user))
            follow.setText("Unfollow");
        else
            follow.setText("Follow");

        hb.getChildren().add(username);
        hb.getChildren().add(spacer);
        hb.getChildren().add(follow);
        box.getChildren().add(hb);

        follow.setOnMouseClicked(event -> {
            String action = follow.getText();
            ConnectionNeo4jDB.connNeo.followUnfollowButton(action, Session.getLoggedUser().getUsername(), user);
            if(action.equals("Follow"))
                follow.setText("Unfollow");
            if(action.equals("Unfollow"))
                follow.setText("Follow");
        });

        username.setOnMouseClicked(event -> {
            try {
                showUserPage(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        follow.getStyleClass().add("button-follow");
        hb.getStyleClass().add("hb-follow");
        box.getStyleClass().add("vbox-follow");

        scrollPage++;
    }

    public void prevPage() {

        box.getChildren().clear();
        scrollPage = Utility.prevPage(scrollPage, nPage, prev);

        if(scrollPage < list.size())
        {
            next.setDisable(false);
            next.setVisible(true);
        }

        show();
    }

    public void nextPage() {

        box.getChildren().clear();

        if (scrollPage + nPage >= list.size()) {
            next.setDisable(true);
            next.setVisible(false);
        }
        prev.setVisible(true);
        prev.setDisable(false);

        show();
    }

    private void showUserPage(String username) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ProfileController.class.getResource("/FXML/Profile.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(new Scene(loader.load()));
            ProfileController controller = loader.getController();
            controller.initialize(username);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
