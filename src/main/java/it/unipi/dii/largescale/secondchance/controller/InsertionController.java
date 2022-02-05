package main.java.it.unipi.dii.largescale.secondchance.controller;

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
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.FileNotFoundException;
import java.util.ArrayList;

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

    public void initialize(String uniq_id) {

        insertionTitle = new Label();
        infoContainer.setVisible(true);
        descriptionContainer.setEditable(false);
        
        Session session = Session.getInstance();
        user = session.getLoggedUser();
        System.out.println("uniq_id controller: " + uniq_id);
        insertion = ConnectionMongoDB.connMongo.findInsertion(uniq_id);
        System.out.println("INSERTION insertionController: " + insertion);
        
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
            if(!ConnectionNeo4jDB.connNeo.deleteInsertion(insertion.getId()))
            {
                Utility.infoBox("Buying product", "Error", "Cannot buy product");
                System.out.println("Error deleting insertion Neo4j");
                ConnectionMongoDB.connMongo.deleteBuyInsertion(user.getUsername(), insertion);
                return;
            }
            //add new purchase to the local array
            Document purchased = new Document()
                    .append("_id", new ObjectId())
                    .append("timestamp", timestamp.getText())
                    .append("seller", insertion.getSeller())
                    .append("reviewed", false)
                    .append("insertion", new Document("image", insertion.getImage_url()).
                            append("price", insertion.getPrice()).
                            append("size", insertion.getSize()).
                            append("status", insertion.getStatus()).
                            append("category", insertion.getCategory()));

            ArrayList<Document> purc = Session.getLogUser().getPurchased();
            purc.add(purchased);
            Session.getLogUser().setPurchased(purc);

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
