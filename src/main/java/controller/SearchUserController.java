package main.java.controller;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import main.java.connection.*;
import main.java.utils.*;
import main.java.utils.Session;
import org.bson.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SearchUserController extends MainController{

    public Button findUsers;
    public Pane prevSugg, nextSugg;

    public TextField us;

    public BorderPane userFind;
    public BorderPane userSugg;

    public ComboBox<String> country;
    public ComboBox<String> rating;

    public Pane prevButton, nextButton;
    Button followSuggested, followSearched;

    public GridPane usersList;
    public GridPane suggList;

    public ArrayList<Document> userFilter;
    public ArrayList<Document> sugg;
    public ArrayList<String> suggFromNeo;

    public int item;
    int scrollPage;
    int k = 15;

    public void initialize() throws IOException {

        usersList = new GridPane();
        item = 0;
        us.setText("");
        country.setValue("country");
        rating.setValue("rating");

        prevButton.setDisable(true);
        nextButton.setDisable(true);
        prevButton.setVisible(false);
        nextButton.setVisible(false);

        suggList = new GridPane();
        sugg = new ArrayList<>();
        suggFromNeo = new ArrayList<>();
        prevSugg.setDisable(true);
        prevSugg.setVisible(false);

        //connection to Neo4j
        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();
        suggFromNeo = connNeo.getSuggestedUsers(Session.getLogUser().getUsername(), Session.getLogUser().getCountry(), k);

        Document d;
        ConnectionMongoDB connMongo = new ConnectionMongoDB();
        Utility.printTerminal("SUGG NEO: " + suggFromNeo.size());

        if(suggFromNeo.size() == 0)
        {
            sugg = connMongo.findTopKRatedUser(10, "Italy");
            Utility.printTerminal("SUGG MONGO: " + sugg.size());
        }
        else {
            for (int i = 0; i < suggFromNeo.size(); i++) {
                d = connMongo.findUserByUsername(suggFromNeo.get(i));
                sugg.add(d);
            }
        }
        Utility.printTerminal("SUGG SIZE: " + sugg.size());

        showSuggestedUsers();
    }

    public void findUsers() throws IOException {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        if (us.getText().equals("")) {

            if (country.getValue().equals("country") && rating.getValue().equals("rating"))
                //Utility.printTerminal("country: " + country + "rating: " + rating);
                return;

            item = 0;
            userFilter = conn.findUserByFilters(country.getValue(), rating.getValue());

            if (userFilter.isEmpty()) {

                Utility.infoBox("There is not a user with this characteristics!", "Advise", "User Advise");
                country.setValue("country");
                rating.setValue("rating");
                return;
            }

            if(userFilter.size() > 1) {
                nextButton.setDisable(false);
                nextButton.setVisible(true);
            }

            showFilteredUsers();

            item++;
            userFind.setCenter(usersList);
            country.setValue("country");
            rating.setValue("rating");
        } else{

            Document users = conn.findUserByUsername(us.getText());

            if (users == null)
                return;

            showSearchedUser(users);

            userFind.setCenter(usersList);
            us.setText("");
            prevButton.setDisable(true);
            nextButton.setDisable(true);
            prevButton.setVisible(false);
            nextButton.setVisible(false);
        }
    }

    private void showFilteredUsers() throws IOException {

        usersList.getChildren().clear();

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {
            Image image = new Image(imageStream);
            ImageView im = new ImageView(image);
            Label username = new Label(userFilter.get(item).getString("username"));
            Label country = new Label(userFilter.get(item).getString("country"));
            Label city = new Label(userFilter.get(item).getString("city"));

            usersList.add(im, 0, 0);
            usersList.add(username, 0, 1);
            usersList.add(country, 0, 2);
            usersList.add(city, 0, 3);

            GridPane.setHalignment(im, HPos.CENTER);
            GridPane.setHalignment(username, HPos.CENTER);
            GridPane.setHalignment(country, HPos.CENTER);
            GridPane.setHalignment(city, HPos.CENTER);

            //a user can not follow itself
            if(!(Session.getLogUser().getUsername().equals(userFilter.get(item).getString("username")))) {
                followSearched = new Button();
                setFollowUnfollowButton(followSearched, userFilter.get(item).getString("username"));
                usersList.add(followSearched, 0, 4);
                GridPane.setHalignment(followSearched, HPos.CENTER);
            }
        }
        usersList.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");
    }

    private void setFollowUnfollowButton(Button follow, String user) {

        follow.setStyle("-fx-background-color: none; -fx-border-color: black; -fx-border-radius: 5;");
        follow.setPrefWidth(90.0);
        follow.setPrefHeight(20.0);
        follow.setDisable(false);
        follow.setVisible(true);
        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();
        if(connNeo.checkIfFollows(Session.getLogUser().getUsername(), user)) {
            follow.setText("Unfollow");
        } else {
            follow.setText("Follow");
        }

        follow.setOnMouseClicked(event -> {
            String action = follow.getText();
            connNeo.followUnfollowButton(action, Session.getLogUser().getUsername(), user);
            if(action.equals("Follow"))
                follow.setText("Unfollow");
            if(action.equals("Unfollow"))
                follow.setText("Follow");
        });

    }

    private void showSearchedUser(Document user) throws IOException {

        usersList.getChildren().clear();

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {
            Image image = new Image(imageStream);
            ImageView im = new ImageView(image);
            Label username = new Label(user.getString("username"));
            Label country = new Label(user.getString("country"));
            Label city = new Label(user.getString("city"));

            usersList.add(im, 0, 0);
            usersList.add(username, 0, 1);
            usersList.add(country, 0, 2);
            usersList.add(city, 0, 3);

            GridPane.setHalignment(im, HPos.CENTER);
            GridPane.setHalignment(username, HPos.CENTER);
            GridPane.setHalignment(country, HPos.CENTER);
            GridPane.setHalignment(city, HPos.CENTER);

            //a user can not follow itself
            if(!(Session.getLogUser().getUsername().equals(user.getString("username")))) {
                followSearched = new Button();
                setFollowUnfollowButton(followSearched, user.getString("username"));
                usersList.add(followSearched, 0, 4);
                GridPane.setHalignment(followSearched, HPos.CENTER);
            }
        }

        usersList.setStyle(
                        "-fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");
    }

    public void showPrevUser() throws IOException {

        item -= 2;

        if (item == 0) {
            prevButton.setDisable(true);
            prevButton.setVisible(false);
        }

        showFilteredUsers();
        item++;

        if (item != userFilter.size() - 1) {
            nextButton.setDisable(false);
            nextButton.setVisible(true);
        }
    }

    public void showNextUser() throws IOException {

        if (item == userFilter.size()-1) {
            nextButton.setDisable(true);
            nextButton.setVisible(false);
        } else{
            nextButton.setDisable(false);
            nextButton.setVisible(true);
        }

        if(item >= 1) {
            prevButton.setVisible(true);
            prevButton.setDisable(false);
        }

        showFilteredUsers();
        item++;
    }

    /*---------------------------------------------------------------*/

    public void addSuggestedUsers(int index, int i) throws IOException {

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {

            Image image = new Image(imageStream);
            ImageView im = new ImageView(image);
            Label username = new Label(sugg.get(index).getString("username"));
            Label country = new Label(sugg.get(index).getString("country"));
            Label city = new Label(sugg.get(index).getString("city"));
            followSuggested = new Button();
            setFollowUnfollowButton(followSuggested, sugg.get(index).getString("username"));

            suggList.add(im, i, 0);
            suggList.add(username, i, 1);
            suggList.add(country, i, 2);
            suggList.add(city, i, 3);
            suggList.add(followSuggested, i, 4);

            GridPane.setHalignment(im, HPos.CENTER);
            GridPane.setHalignment(username, HPos.CENTER);
            GridPane.setHalignment(country, HPos.CENTER);
            GridPane.setHalignment(city, HPos.CENTER);
            GridPane.setHalignment(followSuggested, HPos.CENTER);
        }

        suggList.setStyle(
                        "-fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }

    private void showSuggestedUsers() throws IOException {

        suggList = new GridPane();
        scrollPage = 0;

        for (int i = scrollPage; i < scrollPage + 3; i++) {

            addSuggestedUsers(i, i);
            userSugg.setCenter(suggList);
        }

        scrollPage+=3;
    }

    public void prevSuggestedUsers(MouseEvent mouseEvent) throws IOException {

        suggList.getChildren().clear();
        int row = 0;

        scrollPage-=6;

        nextSugg.setDisable(false);
        nextSugg.setVisible(true);

        if(scrollPage == 0) {

            prevSugg.setDisable(true);
            prevSugg.setVisible(false);
        }

        for(int i = scrollPage; row<3; i++) {
            addSuggestedUsers(i, row);
            row++;
        }

        userSugg.setCenter(suggList);
        scrollPage+=3;
    }

    public void nextSuggestedUsers(MouseEvent mouseEvent) throws IOException {

        suggList.getChildren().clear();
        int row = 0;

        prevSugg.setDisable(false);
        prevSugg.setVisible(true);

        for (int i = scrollPage; i < scrollPage + 3 && row < 3; i++)
        {
            if (i == sugg.size()) {
                nextButton.setDisable(true);
                nextButton.setVisible(false);
                return;
            }

            addSuggestedUsers(i, row);
            row++;
            userSugg.setCenter(suggList);
        }

        scrollPage+= 3;

        if(scrollPage >= sugg.size()-1) {
            nextSugg.setDisable(true);
            nextSugg.setVisible(false);
        }
    }
}
