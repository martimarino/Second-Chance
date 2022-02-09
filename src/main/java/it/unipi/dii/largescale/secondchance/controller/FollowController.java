package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;
import java.io.IOException;
import java.util.ArrayList;

public class FollowController {

    public ArrayList<String> list;
    public BorderPane bp;
    public VBox box;
    public Pane prev, next;
    private int nPage = 8;
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

 /*       if (users.size() == 0) {
            Utility.infoBox("This profile has not following", "Information", "No following!");
            return;
        }
*/
        show();
    }

    public void show() {

        box.getChildren().clear();
        //if there are more than k insertions enable next button
        if (list.size() < nPage) {
            next.setDisable(true);
            next.setVisible(false);
        }
        System.out.println("(show) INDEX: " + scrollPage);

        for (int i = 0; i < nPage && scrollPage < list.size(); i++)
            addUser();

        bp.setCenter(box);

    }

    private void addUser() {

        String user = list.get(scrollPage);

        HBox hb = new HBox();
        VBox det = new VBox();

        Label username = new Label(list.get(scrollPage));
        Button follow = new Button();

        if(ConnectionNeo4jDB.connNeo.checkIfFollows(userLogged.getUsername(), user))
            follow.setText("Unfollow");
        else
            follow.setText("Follow");

        det.getChildren().add(username);
        hb.getChildren().add(det);
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

        String cssLayout =
                "-fx-background-color:  rgb(238, 204, 255)rgb(238, 204, 255);\n" +
                        "-fx-background-radius: 30;\n" +
                        "-fx-text-fill: rgb(255,255,255)";

        follow.setStyle(cssLayout);

        det.setStyle("-fx-padding: 0 110 0 30;");
        hb.setStyle(
                "-fx-padding: 7;" +
                        " -fx-background-color: rgb(230, 230, 255);" );
        box.setStyle(
                "-fx-hgap: 10;" +
                        " -fx-vgap: 10;" +
                        " -fx-max-height: 80;" +
                        " -fx-min-width: 330;" +
                        " -fx-max-width: 400;");
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
