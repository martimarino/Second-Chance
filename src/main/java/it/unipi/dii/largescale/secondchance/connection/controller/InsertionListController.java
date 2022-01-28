package main.java.it.unipi.dii.largescale.secondchance.connection.controller;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.connection.utils.Utility;
import org.bson.Document;

import java.util.ArrayList;

public class InsertionListController {

    public ArrayList<Document> list;
    public BorderPane bp;
    public VBox box;
    public Pane prev, next;
    private int k = 3;
    private int index;

    String user;

    public void initialize(String username) {

        user = username;
        list = ConnectionMongoDB.connMongo.findInsertionBySeller(username);
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

        String cssLayout =
                        "-fx-background-color:  rgb(238, 204, 255)rgb(238, 204, 255);\n" +
                        "-fx-background-radius: 30;\n" +
                "-fx-text-fill: rgb(255,255,255)";

        delete.setStyle(cssLayout);

        det.getChildren().add(status);
        det.getChildren().add(price);
        det.getChildren().add(brand);
        hb.getChildren().add(image);
        hb.getChildren().add(det);
        if(Session.getLogUser().getUsername().equals(user))
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
            if(Utility.confirmDeletion()) {
                ConnectionMongoDB.connMongo.deleteInsertionMongo(id);
                ConnectionNeo4jDB.connNeo.deleteInsertionNeo4J(id);
                initialize(Session.getLogUser().getUsername());
            }
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
