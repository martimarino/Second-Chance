package main.java.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import main.java.connection.ConnectionNeo4jDB;
import main.java.entity.Insertion;
import main.java.connection.ConnectionMongoDB;
import main.java.utils.Utility;

import org.bson.Document;


public class SearchPostController {

    private String id;

    @FXML private TextField postIdField;
    @FXML private TextField sellerIdField;

    @FXML private Button btnDeletePost;

    @FXML private Text category;
    @FXML private Text price;
    @FXML private Text views;

    private String idPost;
    private String sellerIdPost;
    private Document found;

    public void initialize(){
        btnDeletePost.setDisable(true);
    }

    public void searchPost() {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        idPost = postIdField.getText();
        sellerIdPost = sellerIdField.getText();

        if (idPost != null && !idPost.trim().isEmpty())
            found = conn.verifyInsertionInDB(idPost, true);
        else
            found = conn.verifyInsertionInDB(sellerIdPost, false);

        if (found == null || ((idPost == null && idPost.trim().isEmpty()) && (id != null && id.trim().isEmpty()))) {
            Utility.infoBox("The user is not present in the system. Please try again.",
                            "Error!",
                            "User not found!");
        } else {

            Insertion ins = conn.findInsertionDetails(found.getString("_id"));
            System.out.println("Post: " + ins.getDescription());

            category.setText(ins.getCategory());
            price.setText(Double.toString(ins.getPrice()));
            views.setText(Integer.toString(ins.getViews()));

            btnDeletePost.setDisable(false);
        }
    }

    public void deletePost() {

        ConnectionMongoDB connMongo = new ConnectionMongoDB();
        ConnectionNeo4jDB connNeo = new ConnectionNeo4jDB();

        connMongo.deleteInsertionMongo(found.getString("uniq_id"));
        connNeo.deleteInsertionNeo4J(found.getString("uniq_id"));

        System.out.println("Deleted post and relation in MongoDB and Neo4J!");
    }



}
