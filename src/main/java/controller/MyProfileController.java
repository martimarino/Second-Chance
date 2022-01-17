package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javafx.stage.Stage;

import main.java.connection.ConnectionMongoDB;
import main.java.connection.ConnectionNeo4jDB;
import main.java.entity.User;
import main.java.utils.Session;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;

public class MyProfileController extends MainController {

    public GridPane userInfo;

    @FXML public Button btnLogout;
    @FXML public Button btnAddFunds;
    @FXML private Text balanceValue;

    private User user;
    private Session session;

    public void initialize() {

        Session session = Session.getInstance();
        user  = session.getLogUser();

        Label username = new Label(user.getUsername());
        Label name = new Label(user.getName());
        Label email = new Label(user.getEmail());
        Label country = new Label(user.getCountry());
        Label city = new Label(user.getCity());
        Label address = new Label(user.getAddress());
        Label rating = new Label(Double.toString(user.getRating()));
      
        System.out.println(username + " " + name +  " " + email +  " " + country +  " " + city +  " " + address);

        userInfo.add(username, 1,0);
        userInfo.add(name, 1, 1);
        userInfo.add(email, 1, 2);
        userInfo.add(country, 1, 3);
        userInfo.add(city, 1, 4);
        userInfo.add(address, 1, 5);
        userInfo.add(rating, 1, 6);

        updateUserBalance();
    }

    /* ********** FOLLOWERS/ING STATS SECTION ********** */

    public void showUserFollowers() {

        ConnectionNeo4jDB conn = new ConnectionNeo4jDB();
        ArrayList<String> follower = conn.retrieveFollowersByUser(user.getUsername());
        StackPane secondaryLayout = new StackPane();

        for (int i = 0; i < 10; i++) {

            Label x = new Label(follower.get(i));
            x.setTranslateX(10);
            x.setTranslateY(-100 + i*50);
            secondaryLayout.getChildren().add(x);
        }

        Scene secondScene = new Scene(secondaryLayout, 920, 400);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Followers");
        newWindow.setScene(secondScene);
        newWindow.show();
    }

    public void showUserFollowing() {

        ConnectionNeo4jDB conn = new ConnectionNeo4jDB();
        ArrayList<String> following = conn.retrieveFollowingByUser(user.getUsername());

        StackPane secondaryLayout = new StackPane();

        for (int i = 0; i < 10; i++) {

            Label x = new Label(following.get(i));
            x.setTranslateX(10);
            x.setTranslateY(-100 + i*50);
            secondaryLayout.getChildren().add(x);
        }

        Scene secondScene = new Scene(secondaryLayout, 920, 400);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Following");
        newWindow.setScene(secondScene);
        newWindow.show();
    }

    public void showInterestedInsertions() {

        ConnectionNeo4jDB conn = new ConnectionNeo4jDB();
        ArrayList<String> followed_post = conn.retrieveFollowersByUser(user.getUsername());
        StackPane secondaryLayout = new StackPane();

        for (int i = 0; i < 10; i++) {

            Label x = new Label(followed_post.get(i));
            x.setTranslateX(10);
            x.setTranslateY(-100 + i*50);
            secondaryLayout.getChildren().add(x);
        }

        Scene secondScene = new Scene(secondaryLayout, 920, 400);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Followed post");
        newWindow.setScene(secondScene);
        newWindow.show();
    }

    public void showInsertions() {}

    /* ********** BALANCE SECTION ********** */

    public void updateUserBalance() {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        double new_balance = conn.updateBalance(user.getUsername());
        System.out.println("NEW BALANCE HERE: " + new_balance);
        balanceValue.setText(new_balance + "€");
    }

    public void addFundsShow() throws IOException {

        URL url = new File("src/main/resources/FXML/AddFunds.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public void logout() throws IOException {

        session = Session.getInstance();
        session.getLogoutUser();

        // Closing current window
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();

        // Open sign-in window
        Stage primaryStage = new Stage();

        URL url = new File("src/main/resources/FXML/SignIn.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("SecondChance");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
