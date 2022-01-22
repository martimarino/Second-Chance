package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

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

    public void initialize(){
        btnDeletePost.setDisable(true);
    }

    public void searchPost() {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        Document found;

        String idPost = postIdField.getText();
        String sellerIdPost = sellerIdField.getText();

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

    }



}
