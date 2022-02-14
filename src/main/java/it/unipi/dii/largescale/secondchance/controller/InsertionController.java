package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;

import main.java.it.unipi.dii.largescale.secondchance.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;

import java.io.FileNotFoundException;


public class InsertionController {

    @FXML private Label insertionTitle;
    @FXML private TextField descriptionContainer;

    @FXML private Button buy;
    @FXML private Button favourite;

    @FXML private Pane infoContainer;
    @FXML private Pane imgContainer;
    @FXML private Text view;
    @FXML private Text interested;
    @FXML private Text timestamp;
    @FXML private Text color;
    @FXML private Text gender;
    @FXML private Text size;
    @FXML private Text status;
    @FXML private Text category;
    @FXML private Text brand;
    @FXML private Text country;
    @FXML private Text price;
    @FXML private Text seller;

    private Insertion insertion;
    private User user;
    private String type_img;

    public void initialize(Insertion ins) {

        insertion = ins;
        insertionTitle = new Label();
        infoContainer.setVisible(true);
        descriptionContainer.setEditable(false);

        user = Session.getLoggedUser();
        //System.out.println("uniq_id controller: " + uniq_id);

        System.out.println("INSERTION insertionController: " + insertion);
        type_img = "insertion";
        
        try {
            fillInsertionInfo(insertion);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String favouriteText;

        if(!ConnectionNeo4jDB.connNeo.showIfInterested(user.getUsername(), insertion.getId()))
            favouriteText = "Add to favourite";
        else
            favouriteText = "Remove from favourite";

        favourite.setText(favouriteText);
    }

    private void fillInsertionInfo(Insertion insertion) throws FileNotFoundException {

        System.out.println("IMAGE: " + insertion.getImage_url());
        ImageView images = Utility.getGoodImage(insertion.getImage_url(), 300, type_img);
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
            if(!ConnectionNeo4jDB.connNeo.deleteInsertion(insertion.getId()))
            {
                Utility.infoBox("Buying product", "Error", "Cannot buy product");
                System.out.println("Error deleting insertion Neo4j");
                ConnectionMongoDB.connMongo.rollBackInsertion(0, Session.getLoggedUser().getUsername(), insertion);
                return;
            }
            Utility.infoBox("Product bought correctly! ", "User Advise", "Purchase done");

            buy.setText("Already purchased!");
            buy.setDisable(true);
        }
    }

    public void addToFavorite() {

        if(!ConnectionNeo4jDB.connNeo.showIfInterested(user.getUsername(), insertion.getId())) {
            if(!ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), 1))
            {
                Utility.printTerminal("Error add favourite insertion MongoDB");
                Utility.infoBox("Error adding favourite insertion", "Error", "Error adding favourite insertion");
                return;
            }
            if(!ConnectionNeo4jDB.connNeo.likeInsertion(user.getUsername(), insertion.getId()))
            {
                Utility.printTerminal("Error add favourite insertion Neo4j");
                Utility.infoBox("Error adding favourite insertion", "Error", "Error adding favourite insertion");
                ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), -1);
                return;
            }
            favourite.setText("Remove from favourite");
            interested.setText(String.valueOf(Integer.parseInt(interested.getText()) +1));
        }
        else{
            if(!ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), -1))
            {
                Utility.printTerminal("Error remove favourite insertion MongoDB");
                Utility.infoBox("Error removing favourite insertion", "Error", "Error removing favourite insertion");
                return;
            }

            if(!ConnectionNeo4jDB.connNeo.dislikeInsertion(user.getUsername(), insertion.getId()))
            {
                Utility.printTerminal("Error remove favourite insertion Neo4j");
                Utility.infoBox("Error removing favourite insertion", "Error", "Error removing favourite insertion");
                ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), 1);
                return;
            }
            favourite.setText("Add to favourite");
            interested.setText(String.valueOf(Integer.parseInt(interested.getText()) -1));
        }

    }
}
