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

    GridPane viral;
    GridPane feedGrid;

    ArrayList<Document> insertions;
    ArrayList<Document> ins;
    public ArrayList<String> followedFromNeo;

    int scrollPage2;
    int k = 12;
    int scrollPage;

    public void initialize() {

        ConnectionMongoDB connMongo = new ConnectionMongoDB();
        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();

        followedFromNeo = new ArrayList<>();

        insertions = connMongo.findViralInsertions(k);
        showViralInsertions();
        prevButton.setDisable(true);
        prevButton.setVisible(false);

        // new user
       /* if(connNeo.checkNewUser(Session.getLogUser().getUsername())) {
            ins = connMongo.findTopKViewedInsertion(k, "clothing");
            Utility.printTerminal("New user case");
        } else {
            followedFromNeo = connNeo.getFollowedInsertions(Session.getLogUser().getUsername(), k);
            ins = connMongo.followedUserInsertions(followedFromNeo);
        }*/
        followedFromNeo = connNeo.getFollowedInsertions(Session.getLogUser().getUsername(), k);
        ins = connMongo.followedUserInsertions(followedFromNeo);

        if(followedFromNeo.size() < k)
        {
            ArrayList<Document> topK = connMongo.findTopKViewedInsertion(k- followedFromNeo.size(), "clothing");

            for(int i = 0; i < topK.size(); i++) {
                ins.add(topK.get(i));
                System.out.println("TOPK: " + ins);
            }
        }

        Utility.printTerminal("Size of ins: " + ins.size());
        Utility.printTerminal("Size of ins: " + ins.size());

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

    private void addInsertionsViral(int index, int i) {

        ImageView image;

        Label user = new Label("User: " + insertions.get(index).getString("seller"));
        image = Utility.getGoodImage(insertions.get(index).getString("image_url"), 150);
        Label price = new Label(insertions.get(index).getDouble("price") + "€");
        Label status = new Label("Status: " + insertions.get(index).getString("status"));
        Label interested = new Label("Interested: " + insertions.get(index).getInteger("interested"));
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

        user.setOnMouseClicked(event->{
                    try {
                        System.out.println("unique: " + (insertions.get(index).getString("uniq_id") ));
                        showInsertionPage(insertions.get(index).getString("uniq_id"));
                        updateInsertionview(insertions.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event->{
                    try {
                        System.out.println("unique img: " + (insertions.get(index).getString("uniq_id") ));
                        showInsertionPage(insertions.get(index).getString("uniq_id"));
                        updateInsertionview(insertions.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        viral.setStyle(
                        "-fx-padding: 20;\n"  +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;"
        );
    }


    public static void updateInsertionview(String uniq_id) {

        ConnectionMongoDB conn = new ConnectionMongoDB();
        conn.updateNumView(uniq_id);

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

    /* ********* FEED SECTION ********* */

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

        ImageView image;
        System.out.println("INDEX: " + index);
        System.out.println("ins(index): " + ins.get(index));

        Label user = new Label("User: " + ins.get(index).getString("seller"));
    Utility.printTerminal(ins.toString());
        image = Utility.getGoodImage(ins.get(index).getString("image_url"), 150);

        Label price = new Label(ins.get(index).getDouble("price") + "€");
        Label status = new Label("Status: " + ins.get(index).getString("status"));
        Label interested = new Label("Interested: " + ins.get(index).getInteger("interested"));

        feedGrid.add(user, i, 0);
        feedGrid.add(image, i, 1);
        feedGrid.add(status, i, 2);
        feedGrid.add(price, i, 3);
        feedGrid.add(interested, i, 4);

        //System.out.println("FOLLOWED index:" + index);

        GridPane.setHalignment(user, HPos.CENTER);
        GridPane.setHalignment(status, HPos.CENTER);
        GridPane.setHalignment(price, HPos.CENTER);
        GridPane.setHalignment(interested, HPos.CENTER);


        user.setOnMouseClicked(event->{
                    try {
                        System.out.println("unique: " + (insertions.get(index).getString("uniq_id") ));
                        showInsertionPage(ins.get(index).getString("uniq_id"));
                        updateInsertionview(insertions.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event->{
                    try {
                        System.out.println("unique: " + (insertions.get(index).getString("uniq_id") ));
                        showInsertionPage(ins.get(index).getString("uniq_id"));
                        updateInsertionview(insertions.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        feedGrid.setStyle(
                        "-fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }
}



