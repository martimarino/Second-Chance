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
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HomeController {

    public AnchorPane anchorRoot;

    public BorderPane viralInsertions;
    public Pane nextViralButton, prevViralButton;
    HBox viralHBox;
    ArrayList<Document> viralList;
    int scrollViralPage;

    public BorderPane feed;
    public Pane prevFeedButton, nextFeedButton;
    HBox feedBox;
    ArrayList<Document> feedList;
    public ArrayList<String> followedFromNeo;
    int scrollFeedPage;

    //general paramenters
    int k = 12;                     //how many to show
    int nPage = 3;                  //how many per page
    String type_img;

    public void initialize() {

        // viral
        viralList = ConnectionMongoDB.connMongo.findViralInsertions(k);
        prevViralButton.setDisable(true);
        prevViralButton.setVisible(false);
        viralHBox = new HBox();
        viralHBox.setSpacing(10);
        viralInsertions.setCenter(viralHBox);
        scrollViralPage = 0;
        Utility.printTerminal("Size of viral: " + viralList.size());
        showViralInsertions();
        type_img = "insertion";

        // feed
        feedBox = new HBox();
        feedBox.setSpacing(10);
        feed.setCenter(feedBox);
        scrollFeedPage = 0;
        prevFeedButton.setDisable(true);
        prevFeedButton.setVisible(false);

        followedFromNeo = new ArrayList<>();
        followedFromNeo = ConnectionNeo4jDB.connNeo.getFollowedInsertions(Session.getLogUser().getUsername(), k);
        feedList = ConnectionMongoDB.connMongo.followedUserInsertions(followedFromNeo);

        if (followedFromNeo.size() < k) {
            ArrayList<Document> topK = ConnectionMongoDB.connMongo.findTopKViewedInsertion(k - followedFromNeo.size(), "clothing");

            for (int i = 0; i < topK.size(); i++) {
                feedList.add(topK.get(i));
                System.out.println("TOPK: " + feedList);
            }
        }
        Utility.printTerminal("Size of feed: " + feedList.size());
        showFeed();
    }

    /* ********* INSERTION DETAILS ********* */

    public void showInsertionPage(String uniq_id) throws IOException {

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/FXML/Insertion.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertion details");
            stage.setScene(new Scene(loader.load()));
            InsertionController controller = loader.getController();
            controller.initialize(uniq_id);

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
        System.out.println(viralList.get(scrollViralPage).getString("seller"));

        VBox viral = new VBox(user, image, price, status, interested);
        viral.setStyle("-fx-background-color: white; -fx-padding: 8");
        viral.setSpacing(10);

        viralHBox.getChildren().add(viral);

        viral.setOnMouseClicked(event -> {
                    try {
                        System.out.println("unique: " + uniq_id);
                        showInsertionPage(uniq_id);
                        updateInsertionview(uniq_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        scrollViralPage++;
        Utility.printTerminal("SCROLL VIRAL PAGE: " + scrollViralPage);
    }


    public static void updateInsertionview(String uniq_id) {

        ConnectionMongoDB.connMongo.updateNumView(uniq_id);

    }

    private void showViralInsertions() {

        for (int i = 0; i < nPage && i < viralList.size(); i++)
            addInsertionsViral();

    }

    public void prevViralInsertions() {

        viralHBox.getChildren().clear();
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
        System.out.println("SCROLL OUT: " + scrollFeedPage);
        for (int i = 0; i < nPage && i < feedList.size(); i++) {
            System.out.println("SCROLL IN: " + scrollFeedPage + "i: " + i);
            addFeedInsertions();
        }

    }

    public void prevFeedInsertions() {

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
        Utility.printTerminal("UNIQ ID: " + uniq_id);

        Label user = new Label("User: " + feedList.get(scrollFeedPage).getString("seller"));
        Utility.printTerminal(feedList.toString());
        image = Utility.getGoodImage(feedList.get(scrollFeedPage).getString("image_url"), 150, type_img);

        Label price = new Label(feedList.get(scrollFeedPage).getDouble("price") + "€");
        Label status = new Label("Status: " + feedList.get(scrollFeedPage).getString("status"));
        Label interested = new Label("Interested: " + feedList.get(scrollFeedPage).getInteger("interested"));

        VBox feed = new VBox(user, image, price, status, interested);
        feed.setStyle("-fx-background-color: white; -fx-padding: 8");
        feed.setSpacing(10);

        feedBox.getChildren().add(feed);

        feed.setOnMouseClicked(event -> {
                    try {
                        System.out.println("unique: " + uniq_id);
                        showInsertionPage(uniq_id);
                        updateInsertionview(uniq_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        scrollFeedPage++;
    }
}



