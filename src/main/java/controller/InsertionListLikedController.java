package main.java.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import main.java.connection.ConnectionMongoDB;
import main.java.connection.ConnectionNeo4jDB;
import main.java.entity.Insertion;
import main.java.utils.Session;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class InsertionListLikedController {

    private ArrayList<String> followed_ins;
    private ArrayList<Insertion> insertions;
    public BorderPane bp;
    public VBox box;
    public Pane prev, next;
    private int k = 3;
    private int index;

    ConnectionMongoDB connMongo = new ConnectionMongoDB();
    ConnectionNeo4jDB connNeo4J = new ConnectionNeo4jDB();

    public void initialize() {

        followed_ins = connNeo4J.retrieveFollowedInsertionByUser(Session.getLogUser().getUsername());
        insertions = connMongo.findInsertionDetailsNeo4J(followed_ins);
        System.out.println("Insertions: " + insertions.get(0));

        box = new VBox(20);
        index = 0;

        prev.setDisable(true);
        next.setDisable(true);
        prev.setVisible(false);
        next.setVisible(false);

        showInsertionList();
    }

    public void showInsertionList() {

        box.getChildren().clear();

        //if there are more than k insertions enable next button
        if (insertions.size() - index > k) {
            next.setDisable(false);
            next.setVisible(true);
        }
        System.out.println("(show) INDEX: " + index);

        for (int i = 0; i < k && index < insertions.size(); i++)
            addInsertions();

        bp.setCenter(box);

    }

    private void addInsertions() {

        HBox hb = new HBox();
        VBox det = new VBox();
        ImageView image = null;

        try {
            URL url = new URL(insertions.get(index).getImage_url());
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Cookie", "foo=bar");
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            uc.getInputStream();
            BufferedImage img = ImageIO.read(url);
            Image images = SwingFXUtils.toFXImage(img, null);
            image = new ImageView();
            image.setFitHeight(150);
            image.setFitWidth(150);
            image.setImage(images);

        } catch (IOException e) { //case image not valid any more (link with 404 page)
            Image img = new Image("./img/empty.jpg");
            image = new ImageView(img);
            image.setFitHeight(150);
            image.setFitWidth(150);
            image.setPreserveRatio(true);
        }

        Label category = new Label("Category: " + insertions.get(index).getCategory());
        Label price = new Label(insertions.get(index).getPrice() + "€");
        Label views = new Label("Views: " + insertions.get(index).getViews());

        det.getChildren().add(category);
        det.getChildren().add(price);
        det.getChildren().add(views);
        hb.getChildren().add(image);
        hb.getChildren().add(det);
        box.getChildren().add(hb);

        image.setOnMouseClicked(event->{
                    try {
                        SearchInsertionController sic = new SearchInsertionController();
                        sic.showInsertionPage(insertions.get(index).getUniq_id());
                        HomeController.updateInsertionview(insertions.get(index).getUniq_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );


        GridPane.setHalignment(image, HPos.LEFT);
        GridPane.setHalignment(category, HPos.LEFT);
        GridPane.setHalignment(price, HPos.LEFT);
        GridPane.setHalignment(views, HPos.LEFT);

        det.setStyle("-fx-padding: 0 0 0 50;");
        hb.setStyle(
                "-fx-padding: 20;" +
                        " -fx-background-color: rgb(230, 230, 255);");
        box.setStyle(
                " -fx-hgap: 10;" +
                        " -fx-vgap: 10;" +
                        " -fx-max-height: 180;" +
                        " -fx-min-width: 530;" +
                        " -fx-max-width: 600;");

        index++;
        System.out.println("(add) INDEX: " + index);
    }

    public void prevPage() {
        System.out.println("(prev) INDEX: " + index);

        box.getChildren().clear();

        if((index%k) == 0)
            index -= 3;
        else
            index -= (index%k);
        index -= 3;

        System.out.println("(prev) INDEX: " + index);

        if (index == 0) {
            prev.setDisable(true);
            prev.setVisible(false);
        }

        showInsertionList();

    }

    public void nextPage() {

        box.getChildren().clear();

        System.out.println("(next) INDEX: " + index);

        showInsertionList();

        if (index == insertions.size()) {
            next.setDisable(true);
            next.setVisible(false);
        }

        prev.setVisible(true);
        prev.setDisable(false);
    }
}
