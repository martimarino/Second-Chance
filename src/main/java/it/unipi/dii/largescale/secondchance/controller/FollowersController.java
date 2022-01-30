package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;

public class FollowersController {

    public ArrayList<Document> listFollowers;
    public BorderPane bp;
    public VBox box;
    public Pane prev, next;
    private int k = 3;
    private int scrollPage;

    public void initialize(ArrayList<String> followersUsers){

        //System.out.println("User 0: " + followingUsers.get(0));

        listFollowers = new ArrayList<Document>();
        scrollPage = 0;
        box = new VBox(20);

        prev.setVisible(false);
        prev.setDisable(true);

        if (followersUsers.size() == 1) {
            Document user = ConnectionMongoDB.connMongo.findUserByUsername(followersUsers.get(0));
            listFollowers.add(user);

        } else {

            for (int i = 0; i < followersUsers.size() - 1&& i < 10*k; i++) {
                Document user = ConnectionMongoDB.connMongo.findUserByUsername(followersUsers.get(i));
                listFollowers.add(user);
            }
        }
        showFollowers();
    }

    public void showFollowers() {

        box.getChildren().clear();

        //if there are more than k insertions enable next button
        if (listFollowers.size() - scrollPage > k) {
            next.setDisable(false);
            next.setVisible(true);
        }
        System.out.println("(show) INDEX: " + scrollPage);

        for (int i = 0; i < k && i < listFollowers.size(); i++)
            addUser();

        bp.setCenter(box);

    }

    private void addUser() {

        String user = listFollowers.get(scrollPage).getString("username");

        HBox hb = new HBox();
        VBox det = new VBox();

        ImageView image = Utility.getGoodImage(listFollowers.get(scrollPage).getString("image_url"), 150);
        Label username = new Label("Username: " + listFollowers.get(scrollPage).getString("username"));
        Label city = new Label("City: " + listFollowers.get(scrollPage).getString("city"));

        det.getChildren().add(username);
        det.getChildren().add(city);
        hb.getChildren().add(image);
        hb.getChildren().add(det);
        box.getChildren().add(hb);

        image.setOnMouseClicked(event->{
                    try {
                        showUserPage(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        GridPane.setHalignment(image, HPos.LEFT);
        GridPane.setHalignment(username, HPos.LEFT);
        GridPane.setHalignment(city, HPos.LEFT);

        det.setStyle("-fx-padding: 0 100 0 50;");
        hb.setStyle(
                "-fx-padding: 20;" +
                        " -fx-background-color: rgb(230, 230, 255);");
        box.setStyle(
                "-fx-hgap: 10;" +
                        " -fx-vgap: 10;" +
                        " -fx-max-height: 180;" +
                        " -fx-min-width: 530;" +
                        " -fx-max-width: 600;");

        scrollPage++;
        System.out.println("(add) INDEX: " + scrollPage);
    }

    public void prevPage() {

        System.out.println("(prev) INDEX: " + scrollPage);
        box.getChildren().clear();
        scrollPage = Utility.prevPage(scrollPage, k, prev);
        showFollowers();

    }

    public void nextPage() {

        box.getChildren().clear();
        System.out.println("(next) INDEX: " + scrollPage);
        Utility.nextPage(scrollPage + k, listFollowers, next, prev);
        showFollowers();

    }


    private void showUserPage(String username) {

        System.out.println("USERNAME show: " + username);
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
