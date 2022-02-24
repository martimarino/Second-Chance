package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HomeController {

    public AnchorPane anchorRoot;

    public BorderPane viralInsertions;
    public BorderPane feed;

    public Pane nextViralButton, prevViralButton;
    public Pane prevFeedButton, nextFeedButton;

    public HBox viralHBox;
    public HBox feedBox;

    public ArrayList<Document> viralList;
    public ArrayList<Document> feedList;
    public ArrayList<String> followedFromNeo;

    int scrollViralPage;
    int scrollFeedPage;

    //general parameters
    int numInsertions = 12;         // How many to show
    int nPage = 3;                  // How many per page
    String type_img;

    public void initialize() {

        // viral
        scrollViralPage = 0;

        viralList = ConnectionMongoDB.connMongo.findViralInsertions(numInsertions);

        prevViralButton.setDisable(true);
        prevViralButton.setVisible(false);

        viralHBox = new HBox();
        viralHBox.setSpacing(10);

        viralInsertions.setCenter(viralHBox);

        type_img = "insertion";

        showViralInsertions();

        // feed
        scrollFeedPage = 0;

        feedBox = new HBox();
        feedBox.setSpacing(10);

        feed.setCenter(feedBox);

        prevFeedButton.setDisable(true);
        prevFeedButton.setVisible(false);

        followedFromNeo = new ArrayList<>();
        followedFromNeo = ConnectionNeo4jDB.connNeo.getFollowedInsertions(Session.getLoggedUser().getUsername(), numInsertions);

        feedList = ConnectionMongoDB.connMongo.followedUserInsertions(followedFromNeo);

        if (followedFromNeo.size() < numInsertions) {
            ArrayList<Document> topK = ConnectionMongoDB.connMongo.findTopKViewedInsertion(numInsertions - followedFromNeo.size(), "clothing");

            for (Document document : topK)
                feedList.add(document);

        }
        showFeed();
    }

    /* ********* INSERTION DETAILS ********* */

    public void showInsertionPage(String uniq_id) throws IOException {

        Insertion insertion = ConnectionMongoDB.connMongo.findInsertion(uniq_id);

        if (insertion == null) {
            Utility.infoBox("Product already purchased", "Purchased", "Already purchased");
            return;
        }

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/FXML/Insertion.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertion details");
            stage.setScene(new Scene(loader.load()));
            InsertionController controller = loader.getController();
            controller.initialize(insertion, false);

            stage.show();
        }
    }

    /* ********* VIRAL INSERTION SECTION ********* */

    private void addInsertionsViral() {

        String uniq_id = viralList.get(scrollViralPage).get("_id").toString();

        ImageView image;
        Label user = new Label("User: " + viralList.get(scrollViralPage).getString("seller"));
        image = Utility.getGoodImage(viralList.get(scrollViralPage).getString("image_url"), 150, type_img);
        Label price = new Label(viralList.get(scrollViralPage).getDouble("price") + "€");
        Label status = new Label("Status: " + viralList.get(scrollViralPage).getString("status"));
        Label interested = new Label("Interested: " + viralList.get(scrollViralPage).getInteger("interested"));

        VBox viral = new VBox(user, image, price, status, interested);
        viral.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-background-radius: 20px;");
        viral.setSpacing(10);

        viralHBox.getChildren().add(viral);

        viral.setOnMouseClicked(event -> {
                    try {
                        showInsertionPage(uniq_id);
                        ConnectionMongoDB.connMongo.updateNumView(uniq_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        scrollViralPage++;
    }

    private void showViralInsertions() {

        for (int i = 0; i < nPage && i < viralList.size(); i++)
            addInsertionsViral();

    }

    public void prevViralInsertions() {

        viralHBox.getChildren().clear();

        nextViralButton.setDisable(false);
        nextViralButton.setVisible(true);

        scrollViralPage = Utility.prevPage(scrollViralPage, nPage, prevViralButton);
        showViralInsertions();
    }

    public void nextViralInsertions() {

        viralHBox.getChildren().clear();
        Utility.nextPage(scrollViralPage + nPage, viralList, nextViralButton, prevViralButton);
        showViralInsertions();
    }

    /* ********* FEED SECTION ********* */

    public void showFeed() {

        if (feedList.size() < nPage) {
            nextFeedButton.setDisable(true);
            nextFeedButton.setVisible(false);
        }

        for (int i = 0; i < nPage && i < feedList.size(); i++)
            addFeedInsertions();

    }

    public void prevFeedInsertions() {

        nextFeedButton.setDisable(false);
        nextFeedButton.setVisible(true);
        feedBox.getChildren().clear();
        scrollFeedPage = Utility.prevPage(scrollFeedPage, nPage, prevFeedButton);
        showFeed();
    }

    public void nextFeedInsertions() {

        feedBox.getChildren().clear();
        Utility.nextPage(scrollFeedPage + nPage, feedList, nextFeedButton, prevFeedButton);
        showFeed();
    }

    private void addFeedInsertions() {

        ImageView image;
        String uniq_id = feedList.get(scrollFeedPage).get("_id").toString();

        Label user = new Label("User: " + feedList.get(scrollFeedPage).getString("seller"));
        image = Utility.getGoodImage(feedList.get(scrollFeedPage).getString("image_url"), 150, type_img);
        Label price = new Label(feedList.get(scrollFeedPage).getDouble("price") + "€");
        Label status = new Label("Status: " + feedList.get(scrollFeedPage).getString("status"));
        Label interested = new Label("Interested: " + feedList.get(scrollFeedPage).getInteger("interested"));

        VBox feed = new VBox(user, image, price, status, interested);
        feed.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-background-radius: 20px;");
        feed.setSpacing(10);

        feedBox.getChildren().add(feed);

        feed.setOnMouseClicked(event -> {
                    try {
                        showInsertionPage(uniq_id);
                        ConnectionMongoDB.connMongo.updateNumView(uniq_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        scrollFeedPage++;
    }
}



