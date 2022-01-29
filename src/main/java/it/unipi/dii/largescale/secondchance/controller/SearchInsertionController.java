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

    public void initialize(){

        firstRow = new HBox(20);
        secondRow = new HBox(20);
        box = new VBox(20);
        //set buttons
        prev.setDisable(true);
        next.setDisable(true);
        prev.setVisible(false);
        next.setVisible(false);

    }

    public static void showInsertionPage(String uniq_id) throws IOException {

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {

            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SearchInsertionController.class.getResource("/FXML/Insertion.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertion details");
            stage.setScene(new Scene(loader.load()));
            InsertionController controller = loader.getController();
            controller.initialize(uniq_id);
            stage.show();

        }
    }

    public void findInsertion() {

        if (ins.getText().equals("")) {  //filters case

            if (!(size.getValue().equals("size") && price.getValue().equals("price")
                    && gender.getValue().equals("gender") && status.getValue().equals("status")
                    && category.getValue().equals("category") && color.getValue().equals("color"))) {

                //take combobox value and search
                insertionFilter = ConnectionMongoDB.connMongo.findInsertionByFilters(size.getValue(), price.getValue(), gender.getValue(), status.getValue(), category.getValue(), color.getValue());
                if (insertionFilter.isEmpty()) {
                    Utility.infoBox("There is not an insertion with this characteristics!", "Advise", "User Advise");
                    return;
                }
            }
        } else {    //search case
            if(ins.getText().equals("admin"))
                return;
            //search insertion by seller
            insertionFilter = ConnectionMongoDB.connMongo.findInsertionBySeller(ins.getText());

            if (insertionFilter.isEmpty())  //if no article is found try to search for brands
                insertionFilter = ConnectionMongoDB.connMongo.findInsertionByBrand(ins.getText());

            if(insertionFilter.isEmpty()) {
                Utility.infoBox("No results", "Advise", "User Advise");
                return;
            }

            ins.setText("");
        }

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

        if (insertionFilter.size() - index > k) {
            next.setDisable(false);
            next.setVisible(true);
        }

        firstRow.getChildren().clear();
        secondRow.getChildren().clear();
        box.getChildren().clear();

        for (int i = 0; i < k && index < insertionFilter.size(); i++)
            addFilteredInsertions(i);

        insertionFind.setCenter(box);

    }

    private void addFilteredInsertions(int i) {

        VBox vb = new VBox();

        ImageView image;

        Label seller = new Label("Seller: " + insertionFilter.get(index).getString("seller"));

        image = Utility.getGoodImage(insertionFilter.get(index).getString("image_url"), 130);

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
            if(i == ((k/2)-1))
                box.getChildren().add(firstRow);
        } else {
            vb.getChildren().add(seller);
            vb.getChildren().add(image);
            vb.getChildren().add(status);
            vb.getChildren().add(price);
            vb.getChildren().add(brand);
            secondRow.getChildren().add(vb);
            if(i == k-1)
                box.getChildren().add(secondRow);
        }

        seller.setOnMouseClicked(event->{
                    try {
                        showInsertionPage(insertionFilter.get(index).getString("uniq_id"));
                        updateInsertionview(insertionFilter.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event->{
                    try {
                        showInsertionPage(insertionFilter.get(index).getString("uniq_id"));
                        updateInsertionview(insertionFilter.get(index).getString("uniq_id"));
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

    private void updateInsertionview(String uniq_id) {

        ConnectionMongoDB.connMongo.updateNumView(uniq_id);

    }


    public void PrevFilteredInsertion() {

        box.getChildren().clear();

        if((index%k) == 0)
            index -= k;
        else
            index -= (index%k);
        index -= k;

        if (index == 0) {
            prev.setDisable(true);
            prev.setVisible(false);
        }

        showFilteredInsertions();

        insertionFind.setCenter(box);
    }

    public void NextFilteredInsertion() {

        box.getChildren().clear();

        System.out.println("(next) INDEX: " + index);

        showFilteredInsertions();

        if (index == insertionFilter.size()) {
            next.setDisable(true);
            next.setVisible(false);
        }

        prev.setVisible(true);
        prev.setDisable(false);

    }
}
