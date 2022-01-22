package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.connection.*;
import main.java.entity.*;
import main.java.utils.*;
import org.bson.*;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyProfileController extends MainController {

    public GridPane userInfo;
    public GridPane reviews;

    public BorderPane review;

    @FXML public Pane nextReviews, prevReviews;

    private List<Document> listReviews;

    @FXML public Button btnLogout;
    @FXML public Button btnAddFunds;
    @FXML private Text balanceValue;

    public Button followersButton, followingButton;
    public Button interestedInsertionsButton, insertionsButton;

    private User user;
    private Session session;

    int scrollPage2;

    public void initialize() {

        Session session = Session.getInstance();
        user  = session.getLogUser();

        ConnectionMongoDB conn = new ConnectionMongoDB();

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

        listReviews = conn.getReviewsByUser(user.getUsername());
        showReviews();

    }

    public void getReviews() {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        listReviews = conn.getReviewsByUser(user.getUsername());

        System.out.println(listReviews.get(0));
    }

    /* ********** FOLLOWERS/ING STATS SECTION ********** */

    public void showUserFollowers() {

        ConnectionNeo4jDB conn = new ConnectionNeo4jDB();
        ArrayList<String> follower = conn.retrieveFollowersByUser(user.getUsername());
        StackPane secondaryLayout = new StackPane();

        for (int i = 0; i < 10 && follower.size() > i; i++) {

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

        for (int i = 0; i < 10 && following.size() > i; i++) {

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

    public void showInsertions() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/FXML/InsertionList.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setScene(new Scene(loader.load()));

        InsertionListController controller = loader.getController();
        controller.initialize();

        stage.show();
    }

    public void showInsertionsLiked() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/FXML/InsertionListLiked.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);

        stage.setScene(new Scene(loader.load()));

        InsertionListLikedController controller = loader.getController();
        controller.initialize();

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

    public void showReviews() {

        reviews = new GridPane();
        reviews.gridLinesVisibleProperty().set(true); //DEBUG

        ColumnConstraints column1 = new ColumnConstraints(200);
        column1.setPercentWidth(50);
        reviews.getColumnConstraints().addAll(column1);
        scrollPage2 = 0;

        for (int i = 0; i < scrollPage2 + 2 && listReviews.size() > i; i++) {

            addReviews(i, i);
            review.setCenter(reviews);
        }
        scrollPage2 += 2;
    }

    private void addReviews(int index, int i) {

        Label reviewer = new Label(listReviews.get(index).getString("reviewer"));
        Label title = new Label("Title: " + listReviews.get(index).getString("title"));
        Label text = new Label(listReviews.get(index).getString("text"));
        text.setWrapText(true);
        reviews.add(reviewer, i, 0);
        reviews.add(title, i, 1);
        reviews.add(text, i, 2);

        System.out.println("index:" + index);
        GridPane.setHalignment(reviewer, HPos.CENTER);
        GridPane.setHalignment(title, HPos.CENTER);
        GridPane.setHalignment(text, HPos.CENTER);

        reviews.setHgap(10);
        reviews.setVgap(10);
        reviews.setPadding(new Insets(0, 0, 5, 5));
    }

    public void prevReviews() {

        reviews.getChildren().clear();
        int row = 0;

        scrollPage2 -= 4;

        nextReviews.setDisable(false);
        nextReviews.setVisible(true);

        if (scrollPage2 == 0) {
            prevReviews.setDisable(true);
            prevReviews.setVisible(false);
        }

        System.out.println("scrollPage2 in prev: " + scrollPage2);
        for (int i = scrollPage2; row < 2; i++) {
            addReviews(i, row);
            row++;
        }
        review.setCenter(reviews);
        scrollPage2 += 2;


    }

    public void nextReviews() {

        reviews.getChildren().clear();
        int row = 0;

        prevReviews.setDisable(false);
        prevReviews.setVisible(true);

        System.out.println("scrollPage2 in next: " + scrollPage2);
        System.out.println("size of list: " + listReviews.size());

        for (int i = scrollPage2; i < scrollPage2 + 2 && row < 2; i++) {
            if (i == listReviews.size()) {
                nextReviews.setDisable(true);
                nextReviews.setVisible(false);
                return;
            }
            addReviews(i, row);
            row++;
            review.setCenter(reviews);
        }

        scrollPage2 += 2;

        if (scrollPage2 >= listReviews.size() - 1) {
            nextReviews.setDisable(true);
            nextReviews.setVisible(false);
        }

    }
}
