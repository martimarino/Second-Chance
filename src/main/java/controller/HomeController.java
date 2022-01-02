package main.java.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    public Button searchArticlesOrBrands;
    public Button findUsers;
    public BorderPane insertionsFollowedUsers;
    public BorderPane viralInsertions;
    public Button nextButton;
    public Button prevButton;
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

        Label title = new Label("Title: " + insertions.get(index).getString("title"));
        Label price = new Label("Price: " + insertions.get(index).getString("price"));
        Label interested= new Label("Interested: "+ insertions.get(index).getString("interested"));
        viral.add(user, i, 0);
        viral.add(image, i, 1);
        viral.add(title, i, 2);
        viral.add(price, i, 3);
        viral.add(interested, i, 4);
        System.out.println("index:" + index);
        GridPane.setHalignment(user, HPos.CENTER);
        GridPane.setHalignment(title, HPos.CENTER);
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

    public void ShowProfile(MouseEvent mouseEvent) throws IOException {

        URL url = new File("src/main/resources/FXML/Profile.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) profileButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    }

    public void findUsers(MouseEvent mouseEvent) {
    }

    public void SearchByArticleOrBrand(MouseEvent mouseEvent) {
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
