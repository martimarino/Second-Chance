package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.connection.*;
import main.java.entity.*;
import main.java.utils.*;
import org.bson.*;
import javafx.stage.StageStyle;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.ArrayList;
import java.util.List;


public class MyProfileController extends MainController {

    public GridPane userInfo;
    public GridPane reviews;

    public BorderPane review;
    HBox reviewsBox;

    @FXML public Pane nextReviews, prevReviews;
    public Button refreshBtn;
    public Label balanceText;

    private List<Document> listReviews;

    @FXML public Button btnLogout;
    @FXML public Button btnAddFunds;
    @FXML private Text balanceValue;

    public Button followersButton, followingButton;
    public Button interestedInsertionsButton, insertionsButton;

    private User user;
    private Session session;
    private ConnectionMongoDB conn;

    int scrollPage;
    int nPage = 2;

    public void initialize(){

        user  = Session.getLogUser();
        setProfile();
        System.out.println("USERNAME init: " + user.getUsername());
    }


    public void initialize(String us) {

        user = Session.getLogUser();

        if(!us.equals(user.getUsername())) {

            balanceValue.setVisible(false);
            btnAddFunds.setDisable(true);
            btnAddFunds.setVisible(false);
            refreshBtn.setVisible(false);
            refreshBtn.setDisable(true);
            btnLogout.setDisable(true);
            btnLogout.setVisible(false);
            balanceText.setVisible(false);

            Document userSearched = conn.findUserByUsername(us);
            user = new User(userSearched.getString("email"), userSearched.getString("username"), null, userSearched.getString("name"), userSearched.getString("country"), userSearched.getString("city"), userSearched.getString("address"), userSearched.getString("suspended"), userSearched.getDouble("rating"), 0.0, userSearched.getString("image"));

            setProfile();
        }
        System.out.println("USERNAME INIT: " + user.getUsername());
    }

    public void setProfile(){

        prevReviews.setDisable(true);
        prevReviews.setVisible(false);

        reviewsBox = new HBox();
        reviewsBox.setSpacing(100);
        review.setCenter(reviewsBox);

        scrollPage = 0;

        userInfo.getChildren().clear();

        String rate = (Double.isNaN(user.getRating()))? "No reviews" : Double.toString(user.getRating());

        Label username = new Label(user.getUsername());
        Label name = new Label(user.getName());
        Label email = new Label(user.getEmail());
        Label country = new Label(user.getCountry());
        Label city = new Label(user.getCity());
        Label address = new Label(user.getAddress());
        Label rating = new Label(rate);
        Label usernameText = new Label("Username:");
        Label nameText = new Label("Name:");
        Label emailText = new Label("Email:");
        Label countryText = new Label("Country:");
        Label cityText = new Label("City:");
        Label addressText = new Label("Address:");
        Label ratingText = new Label("Rating:");

        System.out.println(username + " " + name +  " " + email +  " " + country +  " " + city +  " " + address);

        userInfo.add(usernameText, 0, 0);
        userInfo.add(nameText, 0, 1);
        userInfo.add(emailText, 0, 2);
        userInfo.add(countryText, 0, 3);
        userInfo.add(cityText, 0, 4);
        userInfo.add(addressText, 0, 5);
        userInfo.add(ratingText, 0, 6);

        userInfo.add(username, 1,0);
        userInfo.add(name, 1, 1);
        userInfo.add(email, 1, 2);
        userInfo.add(country, 1, 3);
        userInfo.add(city, 1, 4);
        userInfo.add(address, 1, 5);
        userInfo.add(rating, 1, 6);

        updateUserBalance();

        listReviews = conn.getReviewsByUser(user.getUsername());
        if (listReviews.size() < 3) {
            System.out.println("Reviews nulle, disattivo i bottoni");
            nextReviews.setDisable(true);
            nextReviews.setVisible(false);
        }
        showReviews();
    }

    public void getReviews() {

        listReviews = ConnectionMongoDB.connMongo.getReviewsByUser(user.getUsername());

        System.out.println(listReviews.get(0));
    }

    /* ********** FOLLOWERS/ING STATS SECTION ********** */

    public void showUserFollowers() {

        ConnectionNeo4jDB conn = new ConnectionNeo4jDB();
        System.out.println("USERNAME : " + user.getUsername());
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

        double new_balance = conn.updateBalance(user.getUsername());
        System.out.println("NEW BALANCE HERE: " + new_balance);
        balanceValue.setText(new_balance + "€");
    }

    public void addFundsShow() throws IOException {
        Stage stage = new Stage();
        Utility.changePage(stage, "AddFunds");
    }

    public void showInsertions() throws IOException {

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InsertionListController.class.getResource("/FXML/InsertionList.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Your insertions");
            InsertionListController controller = loader.getController();
            controller.initialize(user.getUsername());

            stage.show();
        }
    }

    public void showInsertionsLiked() throws IOException {

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InsertionListLikedController.class.getResource("/FXML/InsertionListLiked.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertions you are interested in");
            stage.setScene(new Scene(loader.load()));

            InsertionListLikedController controller = loader.getController();
            controller.initialize(user.getUsername());

            stage.show();
        }
    }

    public void logout() throws IOException {

        session = Session.getInstance();
        session.getLogoutUser();

        // Closing current window
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();

        // Open sign-in window
        Stage primaryStage = new Stage();
        primaryStage.setTitle("SecondChance");
        Utility.changePage(primaryStage, "SignIn");
    }

    public void showReviews() {
        System.out.println("SCROLL OUT REVIEWS: " + scrollPage);
        for (int i = 0; i < nPage && scrollPage < listReviews.size(); i++) {
            System.out.println("SCROLL IN REVIEWS: " + scrollPage + "i: " + i);
            addReviews();
        }
    }

    private void addReviews() {

        Label user = new Label("User: " + listReviews.get(scrollPage).getString("reviewer"));
        user.setTextFill(Color.WHITE);
        Utility.printTerminal(listReviews.toString());

        Label text = new Label(listReviews.get(scrollPage).getString("text"));
        text.setTextFill(Color.WHITE);
        Label title = new Label("Title: " + listReviews.get(scrollPage).getString("title"));
        title.setTextFill(Color.WHITE);
        Label rating = new Label("Rating: " + listReviews.get(scrollPage).getInteger("rating"));
        rating.setTextFill(Color.WHITE);
        text.setWrapText(true);

        VBox feed = new VBox(user, text, title, rating);
        feed.setStyle("-fx-background-color: white; -fx-padding: 8");
        feed.setSpacing(10);
        feed.setPadding(new Insets(5, 5, 5, 5));
        feed.setPrefHeight(100);
        feed.setPrefWidth(300);
        feed.setAlignment(Pos.CENTER);

        String cssLayout =
                "-fx-border-color: rgb(102, 153, 255);\n" +
                        "-fx-background-color: rgb(230, 179, 255);\n" +
                        "-fx-background-radius: 50;\n" +
                        "-fx-border-radius: 50;\n";
        feed.setStyle(cssLayout);

        reviewsBox.getChildren().add(feed);

        scrollPage++;
    }

    public void prevReviews() {
        reviewsBox.getChildren().clear();
        scrollPage = Utility.prevPageReviews(scrollPage, nPage, prevReviews);

        if (scrollPage == 0) {
            nextReviews.setDisable(false);
            nextReviews.setVisible(true);
        }

        showReviews();
    }

    public void nextReviews() {
        reviewsBox.getChildren().clear();
        Utility.nextPage(scrollPage + nPage, (ArrayList<Document>) listReviews, nextReviews, prevReviews);

        if (scrollPage == listReviews.size() - 1) {
            nextReviews.setVisible(false);
            nextReviews.setDisable(true);
        }

        showReviews();
    }
}
