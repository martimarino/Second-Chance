package main.java.controller;

import javafx.fxml.*;
import javafx.scene.control.*;
import main.java.connection.ConnectionMongoDB;
import main.java.connection.ConnectionNeo4jDB;
import main.java.entity.*;
import main.java.utils.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NewInsertionController {

    @FXML private ComboBox<String> categ;
    @FXML private ToggleGroup myToggleGroup;
    @FXML private TextField price;
    @FXML private ComboBox<String> status;
    @FXML private TextField color;
    @FXML private ComboBox<String> size;
    @FXML private TextField brand;
    @FXML private ComboBox<String> country;
    @FXML private TextArea desc;
    @FXML private TextField link;
    int upperbound = 9999999;

    ConnectionMongoDB connMongo = new ConnectionMongoDB();
    ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();

    public void AddNewInsertion() {
        if((categ.getValue().equals("-")) || (status.getValue().equals("-"))
            || (size.getValue().equals("-")) || country.getValue().equals("-")
            || (price.getText().isEmpty()) || link.getText().isEmpty()) {

            Utility.infoBox("You have to fill all the fields", "Error", "Missing values");
            return;
        }

        //check price type
        double p;
        if(!price.getText().matches("[0-9]{1,13}(\\.[0-9]*)?")) {
            Utility.infoBox("Please insert a correct value for price (e.g. 10.99)", "Format error", "Price format is not correct");
            return;
        }
        p = Double.parseDouble(price.getText());

        //generate id;
        Random rand = new Random(); //instance of random class

        //generate random values from 0-9999999
        int int_random = rand.nextInt(upperbound);
        String id = Integer.toString(int_random);
        while (connMongo.findInsertionId(id))
            id = Integer.toString(int_random);

        RadioButton chk = (RadioButton)myToggleGroup.getSelectedToggle(); // Cast object to radio button
        String gender = chk.getText();

        //generate timestamp
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(date);

        Insertion i = new Insertion(id, categ.getValue(), desc.getText(), gender, p, 0, 0, status.getValue(), color.getText(), size.getValue(),
                brand.getText(), country.getValue(), link.getText(), formattedDate, Session.getLogUser().getUsername());
        Utility.printTerminal(i.toString());

        if(!connMongo.addInsertion(i)) {
            Utility.infoBox("Insertion not published, retry.", "Error", "Something went wrong on MongoDB");
            return;
        }
        if((!connNeo.addInsertion(i) || (!connNeo.createPostedRelationship(Session.getLogUser().getUsername(), i.getId())))) {
            Utility.infoBox("Insertion not published, retry.", "Error", "Something went wrong on Neo4j");
            return;
        }

        categ.setValue("-");
        price.setText("");
        status.setValue("-");
        color.setText("");
        size.setValue("-");
        brand.setText("");
        country.setValue("-");
        desc.setText("");
        link.setText("");

        Utility.infoBox("Your insertion has been correctly published!", "Success", "Correctly published.");

    }

}
