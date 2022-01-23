package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.connection.*;
import main.java.entity.*;
import main.java.utils.*;
import org.bson.*;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

public class MyProfileController extends MainController {

    public GridPane userInfo;
    public GridPane reviews;

    public BorderPane review;

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

    int scrollPage2;

    public void initialize(){

        Session session = Session.getInstance();
        user  = session.getLogUser();

        conn = new ConnectionMongoDB();

        setProfile();
        System.out.println("USERNAME init: " + user.getUsername());

    }


    public void initialize(String us) {

        conn = new ConnectionMongoDB();

        Session session = Session.getInstance();
        user  = session.getLogUser();

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
        if (listReviews.size() == 0) {
            System.out.println("Reviews nulle, disattivo i bottoni");
            nextReviews.setDisable(true);
            nextReviews.setVisible(false);
            prevReviews.setDisable(true);
            prevReviews.setVisible(false);
            return;
        }
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

        ConnectionMongoDB conn = new ConnectionMongoDB();

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

        if (listReviews.size() != 0) {
            nextReviews.setDisable(false);
            nextReviews.setVisible(true);
            prevReviews.setDisable(false);
            prevReviews.setVisible(true);
        }

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
