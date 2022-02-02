package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.*;
import javafx.stage.StageStyle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import java.util.ArrayList;
import java.util.List;


public class ProfileController extends MainController {

    public GridPane userInfo;
    public GridPane reviews;

    public BorderPane review;
    public Pane profileImage;
    HBox reviewsBox;

    @FXML public Pane nextReviews, prevReviews;
    public Button refreshBtn;
    public Label balanceText;
    public ImageView imageProfile;

    public ArrayList<String> follower;
    public ArrayList<String> following;

    private List<Document> listReviews;

    @FXML public Button btnLogout;
    @FXML public Button btnAddFunds;
    @FXML private Text balanceValue, titleProfile;

    public Button followersButton, followingButton;
    public Button interestedInsertionsButton, insertionButton;

    private User user;
    private Session session;

    int scrollPage;
    int scrollPage2;
    int nPage = 2;

    public void initialize(){

        user  = Session.getLogUser();
        balanceValue.setText(String.format("%.2f",user.getBalance()) + "€");
        setProfile();
        System.out.println("USERNAME init: " + user.getUsername());
    }

    public void initialize(String us) {

        user = Session.getLogUser();

        scrollPage = 0;
        scrollPage2 = 0;

        if(!us.equals(user.getUsername())) {

            balanceValue.setVisible(false);
            btnAddFunds.setDisable(true);
            btnAddFunds.setVisible(false);
            refreshBtn.setVisible(false);
            refreshBtn.setDisable(true);
            btnLogout.setDisable(true);
            btnLogout.setVisible(false);
            balanceText.setVisible(false);
            interestedInsertionsButton.setDisable(true);
            interestedInsertionsButton.setVisible(false);
            insertionButton.setLayoutY(250);
            insertionButton.setText("View insertion published");
            titleProfile.setText("Profile of " + us);
            titleProfile.setLayoutX(270);
            titleProfile.setLayoutY(70);
            profileImage.setLayoutY(20);
            profileImage.setLayoutX(70);

            Document userSearched = ConnectionMongoDB.connMongo.findUserByUsername(us);
            user = new User(userSearched.getString("email"), userSearched.getString("username"), null, userSearched.getString("name"), userSearched.getString("country"), userSearched.getString("city"), userSearched.getString("address"), userSearched.getString("suspended"), userSearched.getDouble("rating"), 0.0, userSearched.getString("img_profile"));

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
        profileImage.getChildren().clear();

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

        setProfileImage();

        System.out.println("USER:" + username + " " + name +  " " + email +  " " + country +  " " + city +  " " + address + " "+ user.getImage());

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
        following = ConnectionNeo4jDB.connNeo.retrieveFollowingByUser(user.getUsername());
        follower = ConnectionNeo4jDB.connNeo.retrieveFollowersByUser(user.getUsername());
        listReviews = ConnectionMongoDB.connMongo.getReviewsByUser(user.getUsername());
        if (listReviews.size() < 3) {
            System.out.println("Reviews nulle, disattivo i bottoni");
            nextReviews.setDisable(true);
            nextReviews.setVisible(false);
        }
        showReviews();
    }

    public void setProfileImage() {

        Label label = new Label("Change image!");
        label.setVisible(false);

        if (user.getImage().equals("image.png")) {

            System.out.println("HERE!!");

            try {
                FileInputStream imageStream = new FileInputStream("target/classes/img/user.png");
                Image img = new Image(imageStream);
                imageProfile = new ImageView();
                imageProfile.setImage(img);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            imageProfile = Utility.getGoodImage(user.getImage(), 100);
        }

        label.setTranslateX(10);
        label.setTranslateY(45);
        label.setTextFill(Color.RED);
        profileImage.getChildren().add(imageProfile);
        profileImage.getChildren().add(label);

        profileImage.setOnMouseClicked(event->{
                    try {
                        showAddImgProfile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        profileImage.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if (newValue) {
                label.setVisible(true);
            } else {
                label.setVisible(false);
            }
        });
    }

    public void setProfile(String us){

        prevReviews.setDisable(true);
        prevReviews.setVisible(false);

        reviewsBox = new HBox();
        reviewsBox.setSpacing(100);
        review.setCenter(reviewsBox);

        scrollPage = 0;
        scrollPage2 = 0;

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

        following = ConnectionNeo4jDB.connNeo.retrieveFollowingByUser(us);
        follower = ConnectionNeo4jDB.connNeo.retrieveFollowersByUser(us);
        System.out.println("Abdelhakam: " + user.getUsername());
        listReviews = ConnectionMongoDB.connMongo.getReviewsByUser(user.getUsername());

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


        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {

            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FollowersController.class.getResource("/FXML/FollowersPage.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Your insertions");
            FollowersController controller = loader.getController();
            if (follower.size() == 0) {
                Utility.infoBox("You have not followers.", "Information", "No followers!");
                return;
            }
            controller.initialize(follower);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUserFollowing() {

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {

            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FollowingController.class.getResource("/FXML/FollowingPage.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Your insertions");
            FollowingController controller = loader.getController();
            if (following.size() == 0) {
                Utility.infoBox("You have not following.", "Information", "No following!");
                return;
            }
            controller.initialize(following);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showInterestedInsertions() {

        ArrayList<String> followed_post = ConnectionNeo4jDB.connNeo.retrieveFollowersByUser(user.getUsername());
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

        double new_balance = ConnectionMongoDB.connMongo.updateBalance(user.getUsername());
        System.out.println("NEW BALANCE HERE: " + String.format("%.2f",new_balance));
        balanceValue.setText(String.format("%.2f",new_balance) + "€");
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

    public void showAddImgProfile() throws FileNotFoundException {

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png")) {

            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SearchInsertionController.class.getResource("/FXML/UpdateProfileImage.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Update Profile Image");
            stage.setScene(new Scene(loader.load()));
            AddProfileImageController controller = loader.getController();
            controller.initialize();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
