package main.java.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    public Button buy;
    public Button favourite;
    public Pane infoContainer;
    public GridPane infoTable;
    public Label descriptionContainer;
    public Pane imgContainer;
    String insertion_id;
    Double price;
    String seller;
    String image_url;


    public void initialize(String uniq_id) {

        this.insertion_id = uniq_id;
        insertionTitle = new Label();
        infoContainer.setVisible(true);

        ConnectionMongoDB conn = new ConnectionMongoDB();
        Insertion insertion = conn.findInsertion(insertion_id);

        try {
            fillInsertionInfo(insertion);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void fillInsertionInfo(Insertion insertion) throws FileNotFoundException {

        try {
            BufferedImage img = ImageIO.read(new URL(insertion.getImage_url()));
            Image image = SwingFXUtils.toFXImage(img, null);
            ImageView images = new ImageView();
            images.setFitHeight(300);
            images.setFitWidth(300);
            images.setImage(image);
            imgContainer.getChildren().add(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
        price = insertion.getPrice();
        seller = insertion.getSeller();
        image_url = insertion.getImage_url();
        descriptionContainer.setText(insertion.getDescription());

        Text price = new Text(insertion.getPrice() + " " + "€");
        Text seller = new Text(insertion.getSeller());
        Text country = new Text(insertion.getCountry());
        Text status = new Text(insertion.getStatus());
        Text color = new Text(insertion.getColor());
        Text view = new Text(String.valueOf(insertion.getViews()));
        Text interested = new Text(String.valueOf(insertion.getInterested()));
        Text category = new Text(insertion.getCategory());
        Text gender = new Text(insertion.getGender());
        Text size = new Text(insertion.getSize());
        Text timestamp = new Text(insertion.getTimestamp());
        Text brand = new Text(insertion.getBrand());

        Text seller_text = new Text("seller: ");
        Text price_text = new Text("price: ");
        Text country_text = new Text("country: ");
        Text status_text = new Text("status: ");
        Text color_text = new Text("color: ");
        Text view_text = new Text("views: ");
        Text interested_text = new Text("interested: ");
        Text category_text = new Text("category: ");
        Text gender_text = new Text("gender: ");
        Text size_text = new Text("size: ");
        Text timestamp_text = new Text("published: ");
        Text brand_text = new Text("brand: ");

        infoTable.add(price_text, 0, 0);
        infoTable.add(price, 1, 0);
        infoTable.add(seller_text, 0, 1);
        infoTable.add(seller, 1, 1);
        infoTable.add(country_text, 0, 2);
        infoTable.add(country, 1, 2);
        infoTable.add(brand_text, 0, 3);
        infoTable.add(brand, 1, 3);
        infoTable.add(category_text, 0, 4);
        infoTable.add(category, 1, 4);
        infoTable.add(status_text, 0, 5);
        infoTable.add(status, 1, 5);
        infoTable.add(size_text, 0, 6);
        infoTable.add(size, 1, 6);
        infoTable.add(gender_text, 0, 7);
        infoTable.add(gender, 1, 7);
        infoTable.add(color_text, 0, 8);
        infoTable.add(color, 1, 8);
        infoTable.add(timestamp_text, 0, 9);
        infoTable.add(timestamp, 1, 9);
        infoTable.add(view_text, 0, 10);
        infoTable.add(view, 1, 10);
        infoTable.add(interested_text, 0, 11);
        infoTable.add(interested, 1, 11);

        
    }

    public void buyInsertion(MouseEvent mouseEvent) {

        ConnectionMongoDB conn = new ConnectionMongoDB();
        Session session = Session.getInstance();
        User user = session.getLoggedUser();
        if(!conn.buyCurrentInsertion(insertion_id, user.getUsername(), price, seller, image_url))
            Utility.infoBox("Cannot conclude the purchase, something wrong! /n Please retry later","Error", "Error buying product" );
        else
            Utility.infoBox("Product buyed correctly!" , "User Advise", "Purchase done" );
    }

    public void addToFavorite(MouseEvent mouseEvent) {

        Session session = Session.getInstance();
        User user = session.getLoggedUser();

        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();
        ConnectionMongoDB connMongo = new ConnectionMongoDB();

        connNeo.setFavouriteInsertion(user.getUsername(), insertion_id);
        connMongo.updateNumInterested(insertion_id);

    }


}
