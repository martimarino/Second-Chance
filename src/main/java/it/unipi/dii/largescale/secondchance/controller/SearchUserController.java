package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
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

        // Suggested sellers

        suggBox = new HBox(20);
        indexSugg = 0;

        suggList = new ArrayList<>();
        suggFromNeo = new ArrayList<>();
        prevSugg.setDisable(true);
        prevSugg.setVisible(false);
        nextSugg.setDisable(true);
        nextSugg.setVisible(false);

        //connection to Neo4j
        suggFromNeo = ConnectionNeo4jDB.connNeo.getSuggestedUsers(Session.getLogUser().getUsername(), Session.getLogUser().getCountry(), k);

        //if there are less than k suggested, add top k rated to the suggestions

        if(suggFromNeo.size() < k)
        {
            Utility.printTerminal("Not enough suggestions");
            Session session = Session.getInstance();
            User user = session.getLoggedUser();
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

        showSuggestedUsers();
    }

    public void findUsers() throws IOException {

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

            showSearchResults();

            userFind.setCenter(searchBox);
            country.setValue("country");
            rating.setValue("rating");

        } else {        //search case

            searchedList.add(ConnectionMongoDB.connMongo.findUserByUsername(us.getText()));

            if (searchedList.isEmpty())
                Utility.infoBox("This user does not exists.", "Advise", "User Advise");

            showSearchResults();

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
        if(ConnectionNeo4jDB.connNeo.checkIfFollows(Session.getLogUser().getUsername(), user)) {
            follow.setText("Unfollow");
        } else {
            follow.setText("Follow");
        }

        follow.setOnMouseClicked(event -> {
            String action = follow.getText();
            ConnectionNeo4jDB.connNeo.followUnfollowButton(action, Session.getLogUser().getUsername(), user);
            if(action.equals("Follow"))
                follow.setText("Unfollow");
            if(action.equals("Unfollow"))
                follow.setText("Follow");
        });

    }

    /*----------------------------- FIND USERS -----------------------------*/

    public void showSearchResults() throws IOException {

        searchBox.getChildren().clear();

        //if there are more than k insertions enable next button
        if (searchedList.size()-indexSearch > k) {
            nextSearch.setDisable(false);
            nextSearch.setVisible(true);
        }
        System.out.println("(show) INDEX: " + indexSearch);

        for (int i = 0; ((i < k) && (indexSearch < m) && (indexSearch < searchedList.size())); i++)
            addSearchResults();

        userFind.setCenter(searchBox);

    }

    private void addSearchResults() throws IOException {

        VBox vb = new VBox(10);
        Label rating;

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {
            Image image = new Image(imageStream);
            ImageView im = new ImageView(image);
            Label username = new Label(searchedList.get(indexSearch).getString("username"));
            Label country = new Label(searchedList.get(indexSearch).getString("country"));
            Label city = new Label(searchedList.get(indexSearch).getString("city"));
            if(searchedList.get(indexSearch).getDouble("rating") != null)
                rating = new Label(String.format("%.1f", searchedList.get(indexSearch).getDouble("rating")));
            else
                rating = new Label("No reviews");
            vb.getChildren().add(im);
            vb.getChildren().add(username);
            vb.getChildren().add(country);
            vb.getChildren().add(city);
            vb.getChildren().add(rating);

            vb.setOnMouseClicked(event->{
                        try {
                            System.out.println("USERNAME onclick: " + username.getText());
                            showUserPage(username.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );

            GridPane.setHalignment(im, HPos.CENTER);
            GridPane.setHalignment(username, HPos.CENTER);
            GridPane.setHalignment(country, HPos.CENTER);
            GridPane.setHalignment(city, HPos.CENTER);

            //a user can not follow itself
            if(!(Session.getLogUser().getUsername().equals(searchedList.get(indexSearch).getString("username")))) {
                followUnfollow = new Button();
                setFollowUnfollowButton(followUnfollow, searchedList.get(indexSearch).getString("username"));
                vb.getChildren().add(followUnfollow);
                GridPane.setHalignment(followUnfollow, HPos.CENTER);
            }

            searchBox.getChildren().add(vb);
        }
        searchBox.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");
        indexSearch++;
        System.out.println("(add search) INDEX: " + indexSearch);


    }

    public void showPrevUser() throws IOException {

        searchBox.getChildren().clear();

        if((indexSearch%k) == 0)
            indexSearch -= k;
        else
            indexSearch -= (indexSearch%k);
        indexSearch -= k;

        System.out.println("(prev search) INDEX: " + indexSearch);

        if (indexSearch == 0) {
            prevSearch.setDisable(true);
            prevSearch.setVisible(false);
        }

        showSearchResults();
    }

    public void showNextUser() throws IOException {

        searchBox.getChildren().clear();

        System.out.println("(next search) INDEX: " + indexSearch);

        showSearchResults();

        if ((indexSearch == searchedList.size()) || (indexSearch == m)) {
            nextSearch.setDisable(true);
            nextSearch.setVisible(false);
        }

        prevSearch.setVisible(true);
        prevSearch.setDisable(false);
    }

    /*------------------------ SUGGESTED SELLERS FUNCTIONS ------------------------*/

    private void showSuggestedUsers() throws IOException {

        suggBox.getChildren().clear();

        //if there are more than k insertions enable next button
        if (suggList.size()-indexSugg > k) {
            nextSugg.setDisable(false);
            nextSugg.setVisible(true);
        }
        System.out.println("(show sugg) LIST SIZE: " + suggList.size());
        for (int i = 0; ((i < k) && (indexSugg < m) && (indexSugg < suggList.size())); i++)
            addSuggestedUsers();

        userSugg.setCenter(suggBox);
        System.out.println("(show sugg) INDEX: " + indexSugg);
    }

    public void addSuggestedUsers() throws IOException {

        VBox vb = new VBox(10);
        Label rating;

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {
            System.out.println(suggList.get(indexSugg).getString("username"));
            Image image = new Image(imageStream);
            ImageView im = new ImageView(image);
            Label username = new Label(suggList.get(indexSugg).getString("username"));
            Label country = new Label(suggList.get(indexSugg).getString("country"));
            Label city = new Label(suggList.get(indexSugg).getString("city"));
            if(suggList.get(indexSugg).getDouble("rating") != null)
                rating = new Label(String.format("%.1f", suggList.get(indexSugg).getDouble("rating")));
            else
                rating = new Label("No reviews");
            followUnfollow = new Button();
            setFollowUnfollowButton(followUnfollow, suggList.get(indexSugg).getString("username"));

            vb.getChildren().add(im);
            vb.getChildren().add(username);
            vb.getChildren().add(country);
            vb.getChildren().add(city);
            vb.getChildren().add(rating);
            vb.getChildren().add(followUnfollow);
            suggBox.getChildren().add(vb);

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
        }
        suggBox.setStyle(
                "-fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

        indexSugg++;
        System.out.println("(add sugg) INDEX: " + indexSugg);
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

    public void prevSuggestedUsers() throws IOException {

        suggBox.getChildren().clear();

        if((indexSugg%k) == 0)
            indexSugg -= k;
        else
            indexSugg -= (indexSugg%k);
        indexSugg -= k;

        System.out.println("(prev) INDEX: " + indexSugg);

        if (indexSugg == 0) {
            prevSugg.setDisable(true);
            prevSugg.setVisible(false);
        }

        showSuggestedUsers();
    }

    public void nextSuggestedUsers() throws IOException {

        suggBox.getChildren().clear();

        System.out.println("(next) INDEX: " + indexSugg);

        showSuggestedUsers();

        if ((indexSugg == suggList.size()) || (indexSugg == m)) {
            nextSugg.setDisable(true);
            nextSugg.setVisible(false);
        }

        prevSugg.setVisible(true);
        prevSugg.setDisable(false);
    }
}
