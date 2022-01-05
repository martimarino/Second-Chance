package main.java.controller;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import main.java.connection.ConnectionMongoDB;
import main.java.utils.Utility;
import org.bson.Document;
import javax.swing.JComboBox;

import java.util.ArrayList;

public class SearchUserController extends MainController{


    public Button findUsers;
    public TextField us;
    public BorderPane userFind;
    public ComboBox<String> country;
    public ComboBox<String> rating;
    public FXCollections countryCollection;
    public FXCollections ratingCollection;
    public Button prevButton;
    public Button nextButton;
    public GridPane usersList;
    public ArrayList<Document> userFilter;
    public int item;

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


    }

    public void findUsers(MouseEvent mouseEvent) {

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
                Utility.infoBox("There is not a user with this characteristic!", "Advise", "User Advise");
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
        }

    }

    private void showFilteredUsers() {

        usersList.getChildren().clear();

        Label username = new Label(userFilter.get(item).getString("username"));
        Label country = new Label(userFilter.get(item).getString("country"));
        Label city = new Label(userFilter.get(item).getString("city"));

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


    public void showPrevUser(MouseEvent mouseEvent) {

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

    public void showNextUser(MouseEvent mouseEvent) {

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
}
