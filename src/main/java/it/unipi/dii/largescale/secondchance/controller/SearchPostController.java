package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


public class SearchPostController {

    private String id;

    @FXML private TextField postIdField;
    @FXML private TextField sellerIdField;

    @FXML private Button btnDeletePost;

    @FXML private Text category;
    @FXML private Text price;
    @FXML private Text views;

    private Document found;

    public void initialize(){
        btnDeletePost.setDisable(true);
    }

    public void searchPost() throws IOException {

        String idPost = postIdField.getText();
        String sellerIdPost = sellerIdField.getText();

        if (idPost != null && !idPost.trim().isEmpty())
            found = ConnectionMongoDB.connMongo.verifyInsertionInDB(idPost, true);
        else
            found = ConnectionMongoDB.connMongo.verifyInsertionInDB(sellerIdPost, false);

        if (found == null || ((idPost == null && idPost.trim().isEmpty()) && (id != null && id.trim().isEmpty()))) {
            Utility.infoBox("There are not insertion.",
                            "Error!",
                            "No insertions found!");
        } else {


            if (idPost != null && !idPost.trim().isEmpty()) {
                // cerco solo un'inserzione perché è stato inserito solo un codice
                Insertion ins = ConnectionMongoDB.connMongo.findInsertionDetails(found.get("_id").toString());
                System.out.println("Post: " + ins.getDescription());

                category.setText(ins.getCategory());
                price.setText(Double.toString(ins.getPrice()));
                views.setText(Integer.toString(ins.getViews()));

                btnDeletePost.setDisable(false);
            } else {
                // cerco un'array di inserzioni perché è stato inserito un seller_id

                try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
                    Image image = new Image(imageStream);
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(InsertionListLikedController.class.getResource("/FXML/InsertionSearchedByAdmin.fxml"));
                    Stage stage = new Stage(StageStyle.DECORATED);
                    stage.getIcons().add(image);
                    stage.setTitle("Insertions you searched");
                    Scene scene = new Scene(loader.load());
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CSS/InsertionAdminSearchStyle.css")).toExternalForm());
                    stage.setScene(scene);

                    InsertionAdminSearchController controller = loader.getController();
                    controller.initialize(found.getString("seller"));

                    stage.show();
                }

            }
        }
    }

    public void deletePost(String id) {

        if(!ConnectionMongoDB.connMongo.deleteInsertionMongo(id))
        {
            Utility.printTerminal("Error deleting insertion MongoDB");
            Utility.infoBox("Error deleting insertion", "Error", "Error deleting insertion");
            return;
        }
        if(!ConnectionNeo4jDB.connNeo.deleteInsertionNeo4J(id))
        {
            Utility.printTerminal("Error deleting insertion Neo4j");
            Utility.infoBox("Error deleting insertion", "Error", "Error deleting insertion");
            try {
                ConnectionMongoDB.connMongo.addInsertion(Insertion.toInsertion(found));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        System.out.println("Deleted post and relation in MongoDB and Neo4J!");
    }

    public void deleteOnePost() {

        ConnectionMongoDB.connMongo.deleteInsertionMongo(found.get("_id").toString());
        ConnectionNeo4jDB.connNeo.deleteInsertionNeo4J(found.get("_id").toString());

        System.out.println("Deleted post and relation in MongoDB and Neo4J!");
    }
}
