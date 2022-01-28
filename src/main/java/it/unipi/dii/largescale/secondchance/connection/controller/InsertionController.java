package main.java.it.unipi.dii.largescale.secondchance.connection.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.entity.Insertion;

import main.java.it.unipi.dii.largescale.secondchance.connection.entity.User;
import main.java.it.unipi.dii.largescale.secondchance.connection.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.connection.utils.Utility;

import java.io.FileNotFoundException;

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

        insertion = ConnectionMongoDB.connMongo.findInsertion(uniq_id);

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
            Utility.infoBox("Product bought correctly! ", "User Advise", "Purchase done");
            ConnectionNeo4jDB.connNeo.deleteInsertion(insertion.getId());
            buy.setText("Already purchased!");
            buy.setDisable(true);
        }
    }

    public void addToFavorite() {

        if(!ConnectionNeo4jDB.connNeo.showIfInterested(user.getUsername(), insertion.getId())) {
            ConnectionNeo4jDB.connNeo.likeInsertion(user.getUsername(), insertion.getId());
            ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), 1);
            favourite.setText("Remove from favourite");
            interested.setText(String.valueOf(Integer.parseInt(interested.getText()) +1));
        }
        else{
            ConnectionNeo4jDB.connNeo.dislikeInsertion(user.getUsername(), insertion.getId());
            ConnectionMongoDB.connMongo.updateNumInterested(insertion.getId(), -1);
            favourite.setText("Add to favourite");
            interested.setText(String.valueOf(Integer.parseInt(interested.getText()) -1));
        }

        ConnectionNeo4jDB.connNeo.showIfInterested(user.getUsername(), insertion.getId());

        ConnectionNeo4jDB.connNeo.showIfInterested(user.getUsername(), insertion.getId());
    }
}
