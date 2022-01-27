package main.java.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.java.connection.ConnectionMongoDB;
import main.java.connection.ConnectionNeo4jDB;
import main.java.entity.Insertion;

import javax.imageio.ImageIO;
import javafx.scene.image.Image;

import main.java.entity.User;
import main.java.utils.Session;
import main.java.utils.Utility;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class InsertionController {

    public Label insertionTitle;
    public TextField descriptionContainer;

    public Button buy;
    public Button favourite;

    public Pane infoContainer;
    public Pane imgContainer;
    public Text view;
    public Text interested;
    public Text timestamp;
    public Text color;
    public Text gender;
    public Text size;
    public Text status;
    public Text category;
    public Text brand;
    public Text country;
    public Text seller;
    public Text price;
    Insertion insertion;
    User user;
    ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();

    public void initialize(String uniq_id) {

        insertionTitle = new Label();
        infoContainer.setVisible(true);
        descriptionContainer.setEditable(false);
        
        Session session = Session.getInstance();
        user = session.getLoggedUser();

        insertion = ConnectionMongoDB.connMongo.findInsertion(uniq_id);

        try {
            fillInsertionInfo(insertion);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();
        String favouriteText;
        if(!connNeo.showIfInterested(user.getUsername(), insertion.getId()))
            favouriteText = "Add to favourite";
        else
            favouriteText = "Remove from favourite";

        favourite.setText(favouriteText);
    }

    private void fillInsertionInfo(Insertion insertion) throws FileNotFoundException {

        System.out.println("IMAGE: " + insertion.getImage_url());
        ImageView images = Utility.getGoodImage(insertion.getImage_url(), 300);
        imgContainer.getChildren().add(images);

        descriptionContainer.setText(insertion.getDescription());

        price.setText(insertion.getPrice() + " " + "€");
        seller.setText(insertion.getSeller());
        country.setText(insertion.getCountry());
        status.setText(insertion.getStatus());
        color.setText(insertion.getColor());
        view.setText(String.valueOf(insertion.getViews()));
        interested.setText(String.valueOf(insertion.getInterested()));
        category.setText(insertion.getCategory());
        gender.setText(insertion.getGender());
        size.setText(insertion.getSize());
        timestamp.setText(insertion.getTimestamp());
        brand.setText(insertion.getBrand());
        
    }

    public void buyInsertion() {

        if (ConnectionMongoDB.connMongo.buyCurrentInsertion(user.getUsername(), insertion))
        {
            Utility.infoBox("Product bought correctly! ", "User Advise", "Purchase done");
            connNeo.deleteInsertion(insertion.getId());
            buy.setText("Already purchased!");
            buy.setDisable(true);
        }
    }

    public void addToFavorite() {

        connNeo = new ConnectionNeo4jDB();

        if(!connNeo.showIfInterested(user.getUsername(), insertion.getId())) {
            connNeo.likeInsertion(user.getUsername(), insertion.getId());
            ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), 1);
            favourite.setText("Remove from favourite");
            interested.setText(String.valueOf(Integer.parseInt(interested.getText()) +1));
        }
        else{
            connNeo.dislikeInsertion(user.getUsername(), insertion.getId());
            ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), -1);
            favourite.setText("Add to favourite");
            interested.setText(String.valueOf(Integer.parseInt(interested.getText()) -1));
        }

        connNeo.showIfInterested(user.getUsername(), insertion.getId());

        connNeo.showIfInterested(user.getUsername(), insertion.getId());
    }
}
