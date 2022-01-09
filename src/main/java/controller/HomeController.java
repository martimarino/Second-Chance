package main.java.controller;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import main.java.connection.*;
import main.java.utils.*;
import org.bson.Document;

import java.util.ArrayList;

public class HomeController {

    public AnchorPane anchorRoot;
    public BorderPane viralInsertions;
    public Button nextButton;
    public Button prevButton;
    GridPane viral;
    ArrayList<Document> insertions;
    int scrollPage2;

    int k = 12;

    public ArrayList<String> followedFromNeo;
    int scrollPage;
    public Button prevFeedButton;
    public Button nextFeedButton;
    GridPane feedGrid;
    public BorderPane feed;
    ArrayList<Document> ins;

    public void initialize() {

        ConnectionMongoDB connMongo = new ConnectionMongoDB();
        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();
        followedFromNeo = new ArrayList<>();

        insertions = connMongo.findViralInsertions(k);
        showViralInsertions();
        prevButton.setDisable(true);
        prevButton.setVisible(false);

        followedFromNeo = connNeo.getFollowedInsertions(Session.getLogUser().getUsername(), k);
        ins = connMongo.followedUserInsertions(followedFromNeo, k);

        System.out.println("SIZE OF INS: " + ins.size());

        showFeed();
        prevFeedButton.setDisable(true);
        prevFeedButton.setVisible(false);

    }

    private void addInsertionsViral(int index, int i) {

        Label user = new Label("User: " + insertions.get(index).getString("seller"));
        ImageView image = new ImageView(insertions.get(index).getString("image_url"));
        image.setFitHeight(150);
        image.setFitWidth(150);

        Label price = new Label(insertions.get(index).getDouble("price") + "€");
        Label status = new Label("Status: " + insertions.get(index).getString("status"));
        Label interested = new Label("Interested: " + insertions.get(index).getString("interested"));
        viral.add(user, i, 0);
        viral.add(image, i, 1);
        viral.add(status, i, 2);
        viral.add(price, i, 3);
        viral.add(interested, i, 4);
        System.out.println("index:" + index);
        GridPane.setHalignment(user, HPos.CENTER);
        GridPane.setHalignment(status, HPos.CENTER);
        GridPane.setHalignment(price, HPos.CENTER);
        GridPane.setHalignment(interested, HPos.CENTER);

        viral.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }

    private void showViralInsertions() {

        viral = new GridPane();
        scrollPage2 = 0;

        for (int i = scrollPage2; i < scrollPage2 + 3; i++) {

            addInsertionsViral(i, i);
            viralInsertions.setCenter(viral);

        }
        scrollPage2 += 3;
    }

    public void prevViralInsertions() {

        viral.getChildren().clear();
        int row = 0;

        scrollPage2 -= 6;

        nextButton.setDisable(false);
        nextButton.setVisible(true);

        if (scrollPage2 == 0) {
            prevButton.setDisable(true);
            prevButton.setVisible(false);
        }

        for (int i = scrollPage2; row < 3; i++) {
            addInsertionsViral(i, row);
            row++;
        }
        viralInsertions.setCenter(viral);
        scrollPage2 += 3;

    }

    public void nextViralInsertions() {

        viral.getChildren().clear();
        int row = 0;

        prevButton.setDisable(false);
        prevButton.setVisible(true);


        for (int i = scrollPage2; i < scrollPage2 + 3 && row < 3; i++) {
            if (i == insertions.size()) {
                nextButton.setDisable(true);
                nextButton.setVisible(false);
                return;
            }
            addInsertionsViral(i, row);
            row++;
            viralInsertions.setCenter(viral);
        }

        scrollPage2 += 3;

        if (scrollPage2 >= insertions.size() - 1) {
            nextButton.setDisable(true);
            nextButton.setVisible(false);
        }

    }

    /* ************************************************************** */

    public void showFeed() {

        feedGrid = new GridPane();
        scrollPage = 0;

        for (int i = scrollPage; i < scrollPage + 3; i++) {

            addFeedInsertions(i, i);
            feed.setCenter(feedGrid);

        }
        scrollPage += 3;

    }

    public void prevFeedInsertions() {

        feedGrid.getChildren().clear();
        int row = 0;

        scrollPage -= 6;

        nextFeedButton.setDisable(false);
        nextFeedButton.setVisible(true);

        if (scrollPage == 0) {
            prevFeedButton.setDisable(true);
            prevFeedButton.setVisible(false);
        }

        for (int i = scrollPage; row < 3; i++) {
            addFeedInsertions(i, row);
            row++;
        }
        feed.setCenter(feedGrid);
        scrollPage += 3;

    }

    public void nextFeedInsertions() {

        feedGrid.getChildren().clear();
        int row = 0;

        prevFeedButton.setDisable(false);
        prevFeedButton.setVisible(true);

        for (int i = scrollPage; i < scrollPage + 3 && row < 3; i++) {
            if (i == ins.size()) {
                nextFeedButton.setDisable(true);
                nextFeedButton.setVisible(false);
                return;
            }
            addFeedInsertions(i, row);
            row++;
            feed.setCenter(feedGrid);
        }

        scrollPage += 3;

        if (scrollPage >= ins.size() - 1) {
            nextFeedButton.setDisable(true);
            nextFeedButton.setVisible(false);

        }

    }

    private void addFeedInsertions(int index, int i) {

        Label user = new Label("User: " + ins.get(index).getString("seller"));
        ImageView image = new ImageView(ins.get(index).getString("image_url"));
        image.setFitHeight(150);
        image.setFitWidth(150);

        Label price = new Label(ins.get(index).getDouble("price") + "€");
        Label status = new Label("Status: " + ins.get(index).getString("status"));
        Label interested = new Label("Interested: " + ins.get(index).getString("interested"));
        feedGrid.add(user, i, 0);
        feedGrid.add(image, i, 1);
        feedGrid.add(status, i, 2);
        feedGrid.add(price, i, 3);
        feedGrid.add(interested, i, 4);
        System.out.println("FOLLOWED index:" + index);
        GridPane.setHalignment(user, HPos.CENTER);
        GridPane.setHalignment(status, HPos.CENTER);
        GridPane.setHalignment(price, HPos.CENTER);
        GridPane.setHalignment(interested, HPos.CENTER);

        feedGrid.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }

}



