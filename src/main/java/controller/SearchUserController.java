package main.java.controller;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import main.java.connection.*;
import main.java.utils.*;
import org.bson.*;
import main.java.utils.Session;

import java.util.*;

public class SearchUserController extends MainController{

    public Button findUsers;
    public TextField us;
    public BorderPane userFind;
    public ComboBox<String> country;
    public ComboBox<String> rating;
    public Button prevButton, nextButton;
    public GridPane usersList;
    public ArrayList<Document> userFilter;

    public int item;

    public Button prevSugg, nextSugg;
    public BorderPane userSugg;
    public ArrayList<Document> sugg;
    public ArrayList<String> suggFromNeo;
    public GridPane suggList;
    int scrollPage;
    int k = 15;

    public void initialize(){

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
        ConnectionNeo4jDB conn = new ConnectionNeo4jDB();
        suggFromNeo = conn.getSuggestedUsers(Session.getLogUser().getUsername(), Session.getLogUser().getCountry(), k);

        Document d;
        ConnectionMongoDB conn2 = new ConnectionMongoDB();
        for(int i = 0; i < suggFromNeo.size(); i++) {
            d = conn2.findUserByUsername(suggFromNeo.get(i));
            sugg.add(d);
        }

System.out.println("***********************************");
System.out.println("SUGG SIZE: " + sugg.size());

        showSuggestedUsers();

    }

    public void findUsers() {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        if(us.getText().equals(""))
        {
            if(country.getValue().equals("country") && rating.getValue().equals("rating")){
                System.out.println("country: " + country + "rating: " + rating);
                return;
            }
            item = 0;
            userFilter = conn.findUserByFilters(country.getValue(), rating.getValue());
            if(userFilter.isEmpty())
            {
                Utility.infoBox("There is not a user with this characteristics!", "Advise", "User Advise");
                country.setValue("country");
                rating.setValue("rating");
                return;
            }
            if(userFilter.size() > 1)
            {
                nextButton.setDisable(false);
                nextButton.setVisible(true);
            }
            showFilteredUsers();
            item++;
            userFind.setCenter(usersList);

            country.setValue("country");
            rating.setValue("rating");

        }
        else{
            Document users = conn.findUserByUsername(us.getText());
            if(users == null)
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

    private void showFilteredUsers() {

        Utility.showUsers(usersList, userFilter, item);

    }

    private void showSearchedUser(Document user) {

        usersList.getChildren().clear();

        Label username = new Label(user.getString("username"));
        Label country = new Label(user.getString("country"));
        Label city = new Label(user.getString("city"));

        usersList.add(username, 0, 0);
        usersList.add(country, 0, 1);
        usersList.add(city, 0, 2);

        GridPane.setHalignment(username, HPos.CENTER);
        GridPane.setHalignment(country, HPos.CENTER);
        GridPane.setHalignment(city, HPos.CENTER);

        usersList.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");
    }

    public void showPrevUser() {

        item-=2;

        if(item == 0)
        {
            prevButton.setDisable(true);
            prevButton.setVisible(false);
        }

        showFilteredUsers();
        item++;

        if(item != userFilter.size()-1)
        {
            nextButton.setDisable(false);
            nextButton.setVisible(true);
        }
    }

    public void showNextUser() {

        if(item == userFilter.size()-1) {
            nextButton.setDisable(true);
            nextButton.setVisible(false);
        }else{
            nextButton.setDisable(false);
            nextButton.setVisible(true);
        }

        if(item >= 1)
        {
            prevButton.setVisible(true);
            prevButton.setDisable(false);
        }

        showFilteredUsers();
        item++;
    }

    /*---------------------------------------------------------------*/

    public void addSuggestedUsers(int index, int i){

        ImageView image = new ImageView("file: /../../resources/img/user.png");
        Label username = new Label(sugg.get(index).getString("username"));
        Label country = new Label(sugg.get(index).getString("country"));
        Label city = new Label(sugg.get(index).getString("city"));

        suggList.add(image, i, 0);
        suggList.add(username, i, 1);
        suggList.add(country, i, 2);
        suggList.add(city, i, 3);

        GridPane.setHalignment(username, HPos.CENTER);
        GridPane.setHalignment(country, HPos.CENTER);
        GridPane.setHalignment(city, HPos.CENTER);

        suggList.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }

    private void showSuggestedUsers() {

        suggList = new GridPane();
        scrollPage = 0;

        for (int i = scrollPage; i < scrollPage+3; i++) {

            addSuggestedUsers(i, i);
            userSugg.setCenter(suggList);

        }
        scrollPage+=3;
    }

    public void prevSuggestedUsers(MouseEvent mouseEvent) {

        suggList.getChildren().clear();
        int row = 0;

        scrollPage-=6;

        nextSugg.setDisable(false);
        nextSugg.setVisible(true);

        if(scrollPage == 0)
        {
            prevSugg.setDisable(true);
            prevSugg.setVisible(false);
        }

        for(int i = scrollPage; row<3; i++)
        {
            addSuggestedUsers(i, row);
            row++;
        }
        userSugg.setCenter(suggList);
        scrollPage+=3;

    }

    public void nextSuggestedUsers(MouseEvent mouseEvent) {

        suggList.getChildren().clear();
        int row = 0;

        prevSugg.setDisable(false);
        prevSugg.setVisible(true);

        for(int i = scrollPage; i < scrollPage+3 && row<3; i++)
        {
            if(i == sugg.size())
            {
                nextButton.setDisable(true);
                nextButton.setVisible(false);
                return;
            }
            addSuggestedUsers(i, row);
            row++;
            userSugg.setCenter(suggList);
        }

        scrollPage+= 3;

        if(scrollPage >= sugg.size()-1)
        {
            nextSugg.setDisable(true);
            nextSugg.setVisible(false);
        }

    }


}
