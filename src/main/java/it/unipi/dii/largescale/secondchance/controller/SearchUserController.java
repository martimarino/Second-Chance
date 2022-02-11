package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SearchUserController extends MainController{

    Button followUnfollow;

    public Button findUsers;
    public TextField us;

    // FindUsers panel variables

    public BorderPane userFind;                 //pane to fill
    public ComboBox<String> country;
    public ComboBox<String> rating;
    public Pane prevSearch, nextSearch;         //find users arrows
    public HBox searchBox;                      //panel to fill and add to the BorderPane
    public ArrayList<Document> searchedList;      //results of searched or filtered users
    public int indexSearch;                     //next element to show

    // Suggested sellers panel variables

    public BorderPane userSugg;                 //pane to fill
    public Pane prevSugg, nextSugg;             //suggestions arrows
    public HBox suggBox;                       //panel to fill and add to the BorderPane
    public ArrayList<Document> suggList;            //suggested users to show
    public ArrayList<String> suggFromNeo;       //suggestions taken from Neo4j
    public int indexSugg;                       //next element to show

    int k = 3;            //how many to show per page
    int m = 15;           //how many to show

    public void initialize() throws IOException {

        // Find user panel

        searchBox = new HBox(20);
        indexSearch = 0;
        searchedList = new ArrayList<>();

        us.setText("");
        country.setValue("country");
        rating.setValue("rating");

        prevSearch.setDisable(true);
        nextSearch.setDisable(true);
        prevSearch.setVisible(false);
        nextSearch.setVisible(false);

        prevSearch.setOnMouseClicked(event -> prevResults(true, indexSearch, searchBox, prevSearch, nextSearch, searchedList, userFind));
        nextSearch.setOnMouseClicked(event -> nextResults(true, indexSearch, searchBox, prevSearch, nextSearch, searchedList, userFind));

        // Suggested sellers

        suggBox = new HBox(20);
        indexSugg = 0;

        suggList = new ArrayList<>();
        suggFromNeo = new ArrayList<>();
        prevSugg.setDisable(true);
        prevSugg.setVisible(false);
        nextSugg.setDisable(true);
        nextSugg.setVisible(false);

        prevSugg.setOnMouseClicked(event -> prevResults(false, indexSugg, suggBox, prevSugg, nextSugg, suggList, userSugg));
        nextSugg.setOnMouseClicked(event -> nextResults(false, indexSugg, suggBox, prevSugg, nextSugg, suggList, userSugg));

        //connection to Neo4j
        suggFromNeo = ConnectionNeo4jDB.connNeo.getSuggestedUsers(Session.getLoggedUser().getUsername(), Session.getLoggedUser().getCountry(), k);

        //if there are less than k suggested, add top k rated to the suggestions

        if(suggFromNeo.size() < k)
        {
            Utility.printTerminal("Not enough suggestions");
            User user = Session.getLoggedUser();
            ArrayList<Document> temp = ConnectionMongoDB.connMongo.findTopKRatedUser((m-suggFromNeo.size()), user.getCountry());
            for(Document d : temp) {
                suggFromNeo.add(d.getString("username"));
            }
        }

        //fill suggFromNeo list
        for (String s : suggFromNeo) {
            Document d = ConnectionMongoDB.connMongo.findUserByUsername(s);
            suggList.add(d);
        }

        Utility.printTerminal("SUGG SIZE: " + suggList.size());
        showResult(false, suggBox, nextSugg, indexSugg, userSugg, suggList);
    }

    public void findUsers() {

        searchedList.removeAll(searchedList);
        indexSearch = 0;
        prevSearch.setDisable(true);
        nextSearch.setDisable(true);
        prevSearch.setVisible(false);
        nextSearch.setVisible(false);

        //if the TextField is empty apply filters
        if (us.getText().equals("")) {

            //no filters applied
            if (country.getValue().equals("country") && rating.getValue().equals("rating")) {
                prevSearch.setDisable(false);
                prevSearch.setVisible(true);
                nextSearch.setDisable(false);
                nextSearch.setVisible(true);
                searchBox.getChildren().clear();
                return;
            }

            searchedList = ConnectionMongoDB.connMongo.findUserByFilters(country.getValue(), rating.getValue());

            if (searchedList.isEmpty()) {
                Utility.infoBox("There is not a user with this characteristics!", "Advise", "User Advise");
                country.setValue("country");
                rating.setValue("rating");
                return;
            }

            showResult(true, searchBox, nextSearch, indexSearch, userFind, searchedList);

            userFind.setCenter(searchBox);
            country.setValue("country");
            rating.setValue("rating");

        } else {        //search case

            searchedList.add(ConnectionMongoDB.connMongo.findUserByUsername(us.getText()));

            if (searchedList.isEmpty())
                Utility.infoBox("This user does not exists.", "Advise", "User Advise");

            showResult(true, searchBox, nextSearch, indexSearch, userFind, searchedList);

        }

        userFind.setCenter(searchBox);
        us.setText("");

    }

    private void setFollowUnfollowButton(Button follow, String user) {

        follow.setStyle("-fx-background-color: none; -fx-border-color: black; -fx-border-radius: 5;");
        follow.setPrefWidth(90.0);
        follow.setPrefHeight(20.0);
        follow.setDisable(false);
        follow.setVisible(true);
        if(ConnectionNeo4jDB.connNeo.checkIfFollows(Session.getLoggedUser().getUsername(), user)) {
            follow.setText("Unfollow");
        } else {
            follow.setText("Follow");
        }

        follow.setOnMouseClicked(event -> {
            String action = follow.getText();
            ConnectionNeo4jDB.connNeo.followUnfollowButton(action, Session.getLoggedUser().getUsername(), user);
            if(action.equals("Follow"))
                follow.setText("Unfollow");
            if(action.equals("Unfollow"))
                follow.setText("Follow");
        });

    }

    public void showResult(boolean choice, HBox hb, Pane next, int index, BorderPane bp, ArrayList<Document> list) {
        hb.getChildren().clear();

        //if there are more than k insertions enable next button
        if (list.size()-index > k) {
            next.setDisable(false);
            next.setVisible(true);
        }
        System.out.println("(show) INDEX: " + index);

        for (int i = 0; ((i < k) && (index < m) && (index < list.size())); i++)
            addResult(choice, list, hb);

        bp.setCenter(hb);
    }

    public void addResult(boolean choice, ArrayList<Document> list, HBox hb) {

        VBox vb = new VBox(10);
        VBox det = new VBox();
        Label rating;
        int index;

        if(choice)
            index = indexSearch;
        else
            index = indexSugg;

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {
            System.out.println(list.get(index).getString("username"));
            Image image = new Image(imageStream);
            ImageView im = new ImageView(image);
            Label username = new Label(list.get(index).getString("username"));
            Label country = new Label(list.get(index).getString("country"));
            Label city = new Label(list.get(index).getString("city"));
            if(list.get(index).getDouble("rating") != null)
                rating = new Label(String.format("%.1f", list.get(index).getDouble("rating")));
            else
                rating = new Label("No reviews");
            followUnfollow = new Button();
            setFollowUnfollowButton(followUnfollow, list.get(index).getString("username"));

            det.getChildren().add(username);
            det.getChildren().add(country);
            det.getChildren().add(city);
            det.getChildren().add(rating);
            det.setStyle("-fx-padding: 10px;");

            vb.getChildren().add(im);
            vb.getChildren().add(det);
            vb.getChildren().add(followUnfollow);
            vb.setAlignment(Pos.CENTER);
            vb.setStyle("-fx-min-width: 140px; -fx-background-color: white; -fx-padding: 8; -fx-background-radius: 20px;");
            hb.getChildren().add(vb);

            GridPane.setHalignment(im, HPos.CENTER);
            GridPane.setHalignment(username, HPos.CENTER);
            GridPane.setHalignment(country, HPos.CENTER);
            GridPane.setHalignment(city, HPos.CENTER);
            GridPane.setHalignment(followUnfollow, HPos.CENTER);

            vb.setOnMouseClicked(event->{
                        try {
                            System.out.println("USERNAME onclick: " + username.getText());
                            showUserPage(username.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );

            hb.setStyle(
                    "-fx-padding: 20;\n" +
                            "    -fx-hgap: 10;\n" +
                            "    -fx-vgap: 10;");

            if(choice)  //search
                indexSearch++;
            else        //sugg
                indexSugg++;

            System.out.println("(add sugg) INDEX: " + indexSugg);
            System.out.println("(add search) INDEX: " + indexSearch);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void prevResults(boolean choice, int index, HBox hb, Pane prev, Pane next, ArrayList<Document> list, BorderPane bp) {

        hb.getChildren().clear();
        if(choice)  //search
            indexSearch = Utility.prevPage(index, k, prev);
        else        //sugg
            indexSugg = Utility.prevPage(index, k, prev);
        showResult(choice, hb, next, index, bp, list);

    }

    public void nextResults(boolean choice, int index, HBox hb, Pane prev, Pane next, ArrayList<Document> list, BorderPane bp) {

        hb.getChildren().clear();
        showResult(choice, hb, next, index, bp, list);
        Utility.nextPage(index+k, list, next, prev);

    }

    private void showUserPage(String username) {

        System.out.println("USERNAME show: " + username);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ProfileController.class.getResource("/FXML/Profile.fxml"));

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(new Scene(loader.load()));
            ProfileController controller = loader.getController();
            controller.initialize(username);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
