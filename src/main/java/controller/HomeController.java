package main.java.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.connection.*;
import main.java.utils.*;
import org.bson.Document;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HomeController {

    public AnchorPane anchorRoot;

    public BorderPane viralInsertions;
    public BorderPane feed;

    public Pane nextButton, prevButton;
    public Pane prevFeedButton, nextFeedButton;

    //GridPane viral;
    //GridPane feedGrid;
    HBox viralHBox;
    HBox feedBox;

    ArrayList<Document> viralList;
    ArrayList<Document> feedList;
    public ArrayList<String> followedFromNeo;

    int scrollPage2;
    int k = 12;
    int nPage = 3;
    int scrollPage;

    public void initialize() {

        ConnectionMongoDB connMongo = new ConnectionMongoDB();
        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();

        followedFromNeo = new ArrayList<>();

        viralList = connMongo.findViralInsertions(k);
        prevButton.setDisable(true);
        prevButton.setVisible(false);
        viralHBox = new HBox();
        viralHBox.setSpacing(10);
        viralInsertions.setCenter(viralHBox);
        scrollPage2 = 0;
        showViralInsertions();
        //viralHBox.setStyle("-fx-background-color: white; -fx-padding: 5");
        // new user
       /* if(connNeo.checkNewUser(Session.getLogUser().getUsername())) {
            ins = connMongo.findTopKViewedInsertion(k, "clothing");
            Utility.printTerminal("New user case");
        } else {
            followedFromNeo = connNeo.getFollowedInsertions(Session.getLogUser().getUsername(), k);
            ins = connMongo.followedUserInsertions(followedFromNeo);
        }*/
        feedBox = new HBox();
        feedBox.setSpacing(10);
        feed.setCenter(feedBox);
        scrollPage = 0;

        followedFromNeo = connNeo.getFollowedInsertions(Session.getLogUser().getUsername(), k);
        feedList = connMongo.followedUserInsertions(followedFromNeo);

        if (followedFromNeo.size() < k) {
            ArrayList<Document> topK = connMongo.findTopKViewedInsertion(k - followedFromNeo.size(), "clothing");

            for (int i = 0; i < topK.size(); i++) {
                feedList.add(topK.get(i));
                System.out.println("TOPK: " + feedList);
            }
        }

        Utility.printTerminal("Size of ins: " + feedList.size());
        Utility.printTerminal("Size of viral: " + viralList.size());


        showFeed();
        prevFeedButton.setDisable(true);
        prevFeedButton.setVisible(false);

    }

    /* ********* INSERTION SECTION ********* */

    public void showInsertionPage(String uniq_id) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/FXML/Insertion.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setScene(new Scene(loader.load()));

        InsertionController controller = loader.getController();
        controller.initialize(uniq_id);

        stage.show();
    }

    private void addInsertionsViral() {

        ImageView image;
        Label user = new Label("User: " + viralList.get(scrollPage2).getString("seller"));
        image = Utility.getGoodImage(viralList.get(scrollPage2).getString("image_url"), 150);
        Label price = new Label(viralList.get(scrollPage2).getDouble("price") + "€");
        Label status = new Label("Status: " + viralList.get(scrollPage2).getString("status"));
        Label interested = new Label("Interested: " + viralList.get(scrollPage2).getInteger("interested"));
        System.out.println(viralList.get(scrollPage2).getString("seller"));

        VBox viral = new VBox(user, image, price, status, interested);
        viral.setStyle("-fx-background-color: white; -fx-padding: 8");
        viral.setSpacing(10);

        viralHBox.getChildren().add(viral);


        user.setOnMouseClicked(event -> {
                    try {
                        System.out.println("unique: " + (viralList.get(scrollPage2).getString("uniq_id")));
                        showInsertionPage(viralList.get(scrollPage).getString("uniq_id"));
                        updateInsertionview(viralList.get(scrollPage).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event -> {
                    try {
                        System.out.println("unique img: " + (viralList.get(scrollPage).getString("uniq_id")));
                        showInsertionPage(viralList.get(scrollPage).getString("uniq_id"));
                        updateInsertionview(viralList.get(scrollPage).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        scrollPage2++;
    }


    public static void updateInsertionview(String uniq_id) {

        ConnectionMongoDB conn = new ConnectionMongoDB();
        conn.updateNumView(uniq_id);

    }

    private void showViralInsertions() {

        for (int i = 0; i < nPage && viralList.size() > i; i++) {
            addInsertionsViral();
        }

    }

    public void prevViralInsertions() {

        viralHBox.getChildren().clear();
        Utility.prevPage(scrollPage2, nPage, prevButton);
        showViralInsertions();
    }

    public void nextViralInsertions() {

        viralHBox.getChildren().clear();
        Utility.nextPage(scrollPage2 + nPage, viralList, nextButton, prevButton);
        showViralInsertions();
    }

    /* ********* FEED SECTION ********* */

    public void showFeed() {
        System.out.println("SCROLL OUT: " + scrollPage);
        for (int i = 0; i < nPage && i < feedList.size(); i++) {
            System.out.println("SCROLL IN: " + scrollPage + "i: " + i);
            addFeedInsertions();
        }

    }

    public void prevFeedInsertions() {

        feedBox.getChildren().clear();
        Utility.prevPage(scrollPage, nPage, prevFeedButton);
        showFeed();
    }

    public void nextFeedInsertions() {

        feedBox.getChildren().clear();
        Utility.nextPage(scrollPage + nPage, feedList, nextFeedButton, prevFeedButton);
        showFeed();
    }

    private void addFeedInsertions() {

        ImageView image;
        System.out.println("INDEX: " + scrollPage);
        System.out.println("ins(index): " + feedList.get(scrollPage));

        Label user = new Label("User: " + feedList.get(scrollPage).getString("seller"));
        Utility.printTerminal(feedList.toString());
        image = Utility.getGoodImage(feedList.get(scrollPage).getString("image_url"), 150);

        Label price = new Label(feedList.get(scrollPage).getDouble("price") + "€");
        Label status = new Label("Status: " + feedList.get(scrollPage).getString("status"));
        Label interested = new Label("Interested: " + feedList.get(scrollPage).getInteger("interested"));

        VBox feed = new VBox(user, image, price, status, interested);
        feed.setStyle("-fx-background-color: white; -fx-padding: 8");
        feed.setSpacing(10);

        feedBox.getChildren().add(feed);

        user.setOnMouseClicked(event -> {
                    try {
                        System.out.println("unique: " + (feedList.get(scrollPage).getString("uniq_id")));
                        showInsertionPage(feedList.get(scrollPage).getString("uniq_id"));
                        updateInsertionview(feedList.get(scrollPage).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event -> {
                    try {
                        System.out.println("unique: " + (feedList.get(scrollPage).getString("uniq_id")));
                        showInsertionPage(feedList.get(scrollPage).getString("uniq_id"));
                        updateInsertionview(feedList.get(scrollPage).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        scrollPage++;
    }
}



