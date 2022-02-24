package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SearchInsertionController extends MainController{

    public ComboBox<String> size;
    public ComboBox<String> price;
    public ComboBox<String> gender;
    public ComboBox<String> status;
    public ComboBox<String> category;
    public ComboBox<String> color;

    public TextField ins;
    public BorderPane insertionFind;
    public ArrayList<Document> insertionFilter;
    public Pane prev, next;

    private final int k = 6;        //number of elements per page
    private int index;              //index of insertion to show
    public HBox firstRow, secondRow;
    public VBox box;
    String type_img;

    public void initialize(){

        type_img = "insertion";
        firstRow = new HBox(20);
        secondRow = new HBox(20);
        box = new VBox(20);
        index = 0;

        //disable buttons
        prev.setDisable(true);
        next.setDisable(true);
        prev.setVisible(false);
        next.setVisible(false);

    }

    public void findInsertion() {

        //disable buttons
        prev.setDisable(true);
        next.setDisable(true);
        prev.setVisible(false);
        next.setVisible(false);

        if (ins.getText().equals("")) {  //filters case

            //if at least one filter is applied
            if (!(size.getValue().equals("size") && price.getValue().equals("price")
                    && gender.getValue().equals("gender") && status.getValue().equals("status")
                    && category.getValue().equals("category") && color.getValue().equals("color"))) {

                //take combobox value and search
                insertionFilter = ConnectionMongoDB.connMongo.findInsertionByFilters(size.getValue(), price.getValue(), gender.getValue(), status.getValue(), category.getValue(), color.getValue());
                if (insertionFilter.isEmpty())
                    Utility.infoBox("There is not an insertion with this characteristics!", "Advise", "User Advise");
            }
        } else {    //search case

            if(ins.getText().equals("admin"))
                return;

            //search insertion
            insertionFilter = ConnectionMongoDB.connMongo.findInsertionBySearch(ins.getText());

            if(insertionFilter.isEmpty()) {
                Utility.infoBox("No results", "Advise", "User Advise");
                return;
            }

            ins.setText("");
        }

        index = 0;
        showFilteredInsertions();
        insertionFind.setCenter(box);

        size.setValue("size");
        price.setValue("price");
        gender.setValue("gender");
        status.setValue("status");
        category.setValue("category");
        color.setValue("color");
    }

    private void showFilteredInsertions() {

        if (insertionFilter.size() - index > k) {       //more than k to show
            next.setDisable(false);
            next.setVisible(true);
        }

        if(index == 0)
        {
            prev.setDisable(true);
            prev.setVisible(false);
        }

        firstRow.getChildren().clear();
        secondRow.getChildren().clear();
        box.getChildren().clear();

        for (int i = 0; i < k && index < insertionFilter.size(); i++)
           addFilteredInsertions(i);


        box.getChildren().add(firstRow);
        if(index >= k/2)
            box.getChildren().add(secondRow);

        insertionFind.setCenter(box);

    }

    private void addFilteredInsertions(int i) {

        VBox vb = new VBox();
        ImageView image;
        String uniq_id = insertionFilter.get(index).get("_id").toString();

        Label seller = new Label("Seller: " + insertionFilter.get(index).getString("seller"));

        image = Utility.getGoodImage(insertionFilter.get(index).getString("image_url"), 130, type_img);

        Label status = new Label("Status: " + insertionFilter.get(index).getString("status"));
        Label price = new Label(insertionFilter.get(index).getDouble("price") + " " + "€");
        Label brand = new Label("Brand: " + insertionFilter.get(index).getString("brand"));

        if(i<(k/2)) {
            vb.getChildren().add(seller);
            vb.getChildren().add(image);
            vb.getChildren().add(status);
            vb.getChildren().add(price);
            vb.getChildren().add(brand);
            firstRow.getChildren().add(vb);
        } else {
            vb.getChildren().add(seller);
            vb.getChildren().add(image);
            vb.getChildren().add(status);
            vb.getChildren().add(price);
            vb.getChildren().add(brand);
            secondRow.getChildren().add(vb);
        }

        vb.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-background-radius: 20px;");

        seller.setOnMouseClicked(event->{
                    try {
                        showInsertionPage(uniq_id);
                        ConnectionMongoDB.connMongo.updateNumView(uniq_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event->{
                    try {
                        showInsertionPage(uniq_id);
                        ConnectionMongoDB.connMongo.updateNumView(uniq_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        GridPane.setHalignment(seller, HPos.CENTER);
        GridPane.setHalignment(image, HPos.CENTER);
        GridPane.setHalignment(status, HPos.CENTER);
        GridPane.setHalignment(price, HPos.CENTER);
        GridPane.setHalignment(brand, HPos.CENTER);

        box.setStyle(
                "-fx-padding: 50;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");
        index++;
    }

    public void PrevFilteredInsertion() {

        box.getChildren().clear();
        index = Utility.prevPage(index, k, prev);
        if(index < insertionFilter.size())
        {
            next.setDisable(false);
            next.setVisible(true);
        }
        showFilteredInsertions();
        insertionFind.setCenter(box);
    }

    public void NextFilteredInsertion() {

        box.getChildren().clear();
        Utility.nextPage(index, insertionFilter, next, prev);
        showFilteredInsertions();

    }

    public static void showInsertionPage(String uniq_id) throws IOException {       //open a page with insertion details

        Insertion insertion = ConnectionMongoDB.connMongo.findInsertion(uniq_id);

        if(insertion == null) {
            Utility.infoBox("Product already purchased", "Purchased", "Already purchased");
            return;
        }

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {

            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SearchInsertionController.class.getResource("/FXML/Insertion.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertion details");
            stage.setScene(new Scene(loader.load()));
            InsertionController controller = loader.getController();
            controller.initialize(insertion, false);
            stage.show();

        }
    }
}
