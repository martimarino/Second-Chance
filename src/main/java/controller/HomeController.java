package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.java.connection.ConnectionMongoDB;

import org.bson.Document;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class HomeController {


    public Button profileButton;
    public AnchorPane anchorRoot;
    public BorderPane viralInsertions;
    public Button nextButton;
    public Button prevButton;
    @FXML private TextField us;
    GridPane viral;
    ArrayList<Document> insertions;
    int scrollPage2;
    int k = 12;

    public void initialize(){

        ConnectionMongoDB conn = new ConnectionMongoDB();
        conn.followedUserinsertions(); // need to do!!
        insertions = conn.findViralInsertions(k);
        showViralInsertions();

    }

    private void addInsertionsViral(int index, int i){

        Label user = new Label("User: " + insertions.get(index).getString("seller"));
        ImageView image = new ImageView(insertions.get(index).getString("image_url"));
        image.setFitHeight(150);
        image.setFitWidth(150);

        String cur;
        if(insertions.get(index).getString("currency").equals("EUR"))
            cur = "€";
        else
            cur = "$";
        Label price = new Label(insertions.get(index).getString("price") + " " + cur);
        Label status = new Label("Status: " + insertions.get(index).getString("status"));
        Label interested= new Label("Interested: "+ insertions.get(index).getString("interested"));
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

        viral.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");


        //viral.setGridLinesVisible(true);

    }

    private void showViralInsertions() {

        viral = new GridPane();
        scrollPage2 = 0;


        for (int i = scrollPage2; i < scrollPage2+3; i++) {

            addInsertionsViral(i, i);
            viralInsertions.setCenter(viral);

        }
        scrollPage2+=3;
    }

    public void findUsers(MouseEvent mouseEvent) throws IOException {

        ConnectionMongoDB conn = new ConnectionMongoDB();


    }

    public void PrevViralInsertion(MouseEvent mouseEvent) {

        viral.getChildren().clear();
        int row = 0;
        if(scrollPage2 == 0)
            scrollPage2 = insertions.size()-3;
        else
            scrollPage2-=3;

        for(int i = scrollPage2; row<3; i++)
        {
            addInsertionsViral(i, row);
            row++;
        }
        viralInsertions.setCenter(viral);


    }

    public void nextViralInsertion(MouseEvent mouseEvent) {

        viral.getChildren().clear();
        int row = 0;

        for(int i = scrollPage2; i < scrollPage2+3 && row<3; i++)
        {
            if(i == insertions.size()) {
                    i = 0;
                    scrollPage2 = 0;
            }
            addInsertionsViral(i, row);
            row++;
        }
        scrollPage2+= 3;
        viralInsertions.setCenter(viral);

    }
}
