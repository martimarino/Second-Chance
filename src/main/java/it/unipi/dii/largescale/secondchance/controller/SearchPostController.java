package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class SearchPostController {

    private String id;

    @FXML private TextField countryField;
    @FXML private TextField sellerField;
    @FXML private TextField categoryField;

    @FXML private Text category;
    @FXML private Text price;
    @FXML private Text views;

    private Document found;

    private final String[] countries = new String[]{"Italy", "Canada", "Spain", "Austria", "Germany", "France", "Brazil", "Netherlands", "Poland", "Ireland", "United Kingdom (Great Britain)"};
    private final String[] categories = new String[]{"clothing","accessories", "bags","beauty", "house", "jewelry", "kids", "shoes"};

    public void initialize(){

        sellerField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            if (!Objects.equals(newValue, "")) {
                countryField.setText("");
                categoryField.setText("");
            }
        });

        countryField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            if (!Objects.equals(newValue, ""))
                sellerField.setText("");

        });

        categoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            if (!Objects.equals(newValue, ""))
                sellerField.setText("");

        });
    }

    public void searchPost() throws IOException {

        String country = countryField.getText();
        String seller = sellerField.getText();
        String category = categoryField.getText();

        if (Objects.equals(seller, "")) {
            if (!Arrays.asList(categories).contains(category)) {
                Utility.infoBox("Please insert a valid category", "Error", "Category not found!");
                return;
            }

            if (!Arrays.asList(countries).contains(country)) {
                Utility.infoBox("Please insert a valid country", "Error", "Country not found!");
                return;
            }
        }

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InsertionListLikedController.class.getResource("/FXML/InsertionSearchedByAdmin.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertions you searched");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CSS/InsertionAdminSearchStyle.css")).toExternalForm());
            stage.setScene(scene);

            InsertionAdminSearchController controller = loader.getController();
            if (!Objects.equals(country, "") && !Objects.equals(category, "") && Objects.equals(seller, ""))
                controller.initialize("",category, country);
            else
                controller.initialize(seller,"", "");

            stage.show();
        }

    }

    public void deletePost(String id) {

        if(!ConnectionMongoDB.connMongo.deleteInsertionMongo(id))
        {
            Utility.printTerminal("Error deleting insertion MongoDB");
            Utility.infoBox("Error deleting insertion", "Error", "Error deleting insertion");
            return;
        }
        if(!ConnectionNeo4jDB.connNeo.deleteInsertionNeo4J(id))
        {
            Utility.printTerminal("Error deleting insertion Neo4j");
            Utility.infoBox("Error deleting insertion", "Error", "Error deleting insertion");
            try {
                ConnectionMongoDB.connMongo.addInsertion(Insertion.toInsertion(found));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        System.out.println("Deleted post and relation in MongoDB and Neo4J!");
    }

    public void deleteOnePost() {

        ConnectionMongoDB.connMongo.deleteInsertionMongo(found.get("_id").toString());
        ConnectionNeo4jDB.connNeo.deleteInsertionNeo4J(found.get("_id").toString());

        System.out.println("Deleted post and relation in MongoDB and Neo4J!");
    }
}
