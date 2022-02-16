package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.*;
import javafx.scene.control.*;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Session;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void AddNewInsertion() {
        if((categ.getValue().equals("-")) || (status.getValue().equals("-"))
                ||(color.getText().isEmpty()) || (brand.getText().isEmpty())
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
        ObjectId id = new ObjectId();
        RadioButton chk = (RadioButton)myToggleGroup.getSelectedToggle(); // Cast object to radio button
        String gender = chk.getText();

        //generate timestamp
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        Insertion i = new Insertion(id.toString(), categ.getValue(), desc.getText(), gender, p, 0, 0, status.getValue(), color.getText(), size.getValue(),
                brand.getText(), country.getValue(), link.getText(), formattedDate, Session.getLoggedUser().getUsername());

        //MongoDB failure
        try {
            ConnectionMongoDB.connMongo.addInsertion(i);
        } catch (Exception e) {
            Utility.infoBox("Insertion not published, retry.", "Error", "Something went wrong on MongoDB");
            return;
        }
        //Neo4j failure
        if(!ConnectionNeo4jDB.connNeo.addInsertion(i)) {
            Utility.infoBox("Insertion not published, retry.", "Error", "Something went wrong on Neo4j");
            ConnectionMongoDB.connMongo.deleteInsertionMongo(i.getId());
            return;
        }
        if((!ConnectionNeo4jDB.connNeo.createPostedRelationship(Session.getLoggedUser().getUsername(), i.getId()))){
            Utility.infoBox("Insertion not published, retry.", "Error", "Something went wrong on Neo4j");
            ConnectionNeo4jDB.connNeo.deleteInsertionNeo4J(i.getId());
            ConnectionMongoDB.connMongo.deleteInsertionMongo(i.getId());
            return;
        }

        //clear fields
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
