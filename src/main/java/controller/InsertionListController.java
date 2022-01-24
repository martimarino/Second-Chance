package main.java.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.java.connection.*;
import main.java.utils.*;

import org.bson.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class InsertionListController {

    public ArrayList<Document> list;
    public BorderPane bp;
    public VBox box;
    public Pane prev, next;
    private int k = 3;
    private int index;

    ConnectionMongoDB connMongo = new ConnectionMongoDB();
    ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();

    public void initialize(String username) {

        list = connMongo.getAllUserIns(username);
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
        if (list.size()-index > k) {
            next.setDisable(false);
            next.setVisible(true);
        }
        System.out.println("(show) INDEX: " + index);

        for (int i = 0; i < k && index < list.size(); i++)
            addInsertions();

        bp.setCenter(box);

    }

    private void addInsertions() {

        String id = list.get(index).getString("uniq_id");

        HBox hb = new HBox();
        VBox det = new VBox();

        ImageView image = Utility.getGoodImage(list.get(index).getString("image_url"), 150);
        Label status = new Label("Status: " + list.get(index).getString("status"));
        Label price = new Label(list.get(index).getDouble("price") + " " + "€");
        Label brand = new Label("Brand: " + list.get(index).getString("brand"));
        Button delete = new Button("Delete");

        det.getChildren().add(status);
        det.getChildren().add(price);
        det.getChildren().add(brand);
        hb.getChildren().add(image);
        hb.getChildren().add(det);
        hb.getChildren().add(delete);
        box.getChildren().add(hb);

        image.setOnMouseClicked(event->{
                    try {
                        SearchInsertionController sic = new SearchInsertionController();
                        sic.showInsertionPage(id);
                        HomeController.updateInsertionview(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        delete.setOnMouseClicked(event -> {
            connMongo.deleteInsertionMongo(id);
            connNeo.deleteInsertionNeo4J(id);
            initialize(Session.getLogUser().getUsername());
        });

        GridPane.setHalignment(image, HPos.LEFT);
        GridPane.setHalignment(status, HPos.LEFT);
        GridPane.setHalignment(price, HPos.LEFT);
        GridPane.setHalignment(brand, HPos.LEFT);
        GridPane.setHalignment(delete, HPos.RIGHT);

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

        if (index == list.size()) {
            next.setDisable(true);
            next.setVisible(false);
        }

        prev.setVisible(true);
        prev.setDisable(false);
    }
}
