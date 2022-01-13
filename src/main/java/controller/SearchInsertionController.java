package main.java.controller;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import main.java.connection.*;
import main.java.utils.*;
import org.bson.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class SearchInsertionController extends MainController{

    public ComboBox<String> size;
    public ComboBox<String> price;
    public ComboBox<String> gender;
    public ComboBox<String> status;
    public ComboBox<String> category;
    public ComboBox<String> color;

    public TextField ins;

    public GridPane insertionList;

    public BorderPane insertionFind;

    public ArrayList<Document> insertionFilter;

    public Button prevSearch, nextSearch;

    public int item, scrollPage;

    public void initialize(){

        insertionList = new GridPane();
        item = 0;

        prevSearch.setDisable(true);
        nextSearch.setDisable(true);
        prevSearch.setVisible(false);
        nextSearch.setVisible(false);
    }

    public void showInsertionPage(String uniq_id) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/FXML/Insertion.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));
        InsertionController controller = loader.getController();
        controller.initialize(uniq_id);
        stage.show();
    }

    public void findInsertion(MouseEvent mouseEvent) {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        item = 0;

        if (ins.getText().equals("")) {  //filters case

            if (!(size.getValue().equals("size") && price.getValue().equals("price")
                    && gender.getValue().equals("gender") && status.getValue().equals("status")
                    && category.getValue().equals("category") && color.getValue().equals("color"))) {

                    //take combobox value and search
                    insertionFilter = conn.findInsertionByFilters(size.getValue(), price.getValue(), gender.getValue(), status.getValue(), category.getValue(), color.getValue());
                    if (insertionFilter.isEmpty()) {
                        Utility.infoBox("There is not an insertion with this characteristics!", "Advise", "User Advise");
                        return;
                    }

                    if (insertionFilter.size() > 1) {
                        nextSearch.setDisable(false);
                        nextSearch.setVisible(true);
                    }

                    showFilteredInsertions();
                    item++;
                    insertionFind.setCenter(insertionList);
            }
        }else {
            //search case

            //search insertion by seller
            insertionFilter = conn.findInsertionBySeller(ins.getText());

            if (insertionFilter.isEmpty())  //if no article is found try to search for brands
                //search by brand
                insertionFilter = conn.findInsertionByBrand(ins.getText());

            if(insertionFilter.isEmpty()) {
                Utility.infoBox("No results", "Advise", "User Advise");
                return;
            }

            showFilteredInsertions();
            insertionFind.setCenter(insertionList);
            ins.setText("");
        }

        size.setValue("size");
        price.setValue("price");
        gender.setValue("gender");
        status.setValue("status");
        category.setValue("category");
        color.setValue("color");
    }

    private void showFilteredInsertions() {

        insertionList = new GridPane();
        scrollPage = 0;

        for (int i = scrollPage; i < scrollPage+3; i++)
            addFilteredInsertions(i, i, 0);

        for (int i = scrollPage; i < scrollPage+3; i++)
            addFilteredInsertions(i+3, i, 5);

        insertionFind.setCenter(insertionList);
        scrollPage+=6;
    }

    private void addFilteredInsertions(int index, int i, int j) {

        ImageView image = null;

        Label seller = new Label("Seller: " + insertionFilter.get(index).getString("seller"));

        try {

            URL url = new URL(insertionFilter.get(index).getString("image_url") );
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Cookie", "foo=bar");
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            //uc.setReadTimeout(5000);
            //uc.setConnectTimeout(5000);
            uc.getInputStream();
            //  HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //connection.setRequestMethod("POST");
            BufferedImage img = ImageIO.read(url);
            Image images = SwingFXUtils.toFXImage(img, null);
            image = new ImageView();
            image.setFitHeight(100);
            image.setFitWidth(100);
            image.setImage(images);

        } catch (IOException e) { //case image not valid any more (link with 404 page)
            //e.printStackTrace();
            Image img = new Image("./img/empty.jpg");
            image = new ImageView(img);
            image.setFitHeight(100);
            image.setFitWidth(100);
            image.setPreserveRatio(true);
        }

        Label status = new Label("Status: " + insertionFilter.get(index).getString("status"));
        Label price = new Label(insertionFilter.get(index).getDouble("price") + " " + "€");
        Label brand = new Label("Brand: " + insertionFilter.get(index).getString("brand"));

        insertionList.add(seller, i, j);
        insertionList.add(image, i, j+1);
        insertionList.add(status, i, j+2);
        insertionList.add(price, i, j+3);
        insertionList.add(brand, i, j+4);

        seller.setOnMouseClicked(event->{
                    try {
                        showInsertionPage(insertionFilter.get(index).getString("uniq_id"));
                        updateInsertionview(insertionFilter.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        image.setOnMouseClicked(event->{
                    try {
                        showInsertionPage(insertionFilter.get(index).getString("uniq_id"));
                        updateInsertionview(insertionFilter.get(index).getString("uniq_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );


        GridPane.setHalignment(seller, HPos.CENTER);
        GridPane.setHalignment(image, HPos.CENTER);
        GridPane.setHalignment(status, HPos.CENTER);
        GridPane.setHalignment(price, HPos.CENTER);
        GridPane.setHalignment(brand, HPos.CENTER);

        insertionList.setStyle(
                        "-fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");
    }

    private void updateInsertionview(String uniq_id) {

        ConnectionMongoDB conn = new ConnectionMongoDB();
        conn.updateNumView(uniq_id);

    }


    public void PrevFilteredInsertion(MouseEvent mouseEvent) {

        insertionList.getChildren().clear();
        int row = 0;

        if (scrollPage == 6) {
            prevSearch.setDisable(true);
            prevSearch.setVisible(false);
        }

        if(scrollPage == 0)
            scrollPage = insertionFilter.size() - 6;
        else
            scrollPage-=6;


        for (int i = scrollPage; row < 3; i++) {

            addFilteredInsertions(i, row, 0);
            row++;
        }

        row = 0;

        for (int i = scrollPage; row<3; i++) {

            addFilteredInsertions(i+3, row, 5);
            row++;
        }

        insertionFind.setCenter(insertionList);
    }

    public void NextFilteredInsertion(MouseEvent mouseEvent) {

        insertionList.getChildren().clear();
        int row = 0;

        for (int i = scrollPage; i < scrollPage+3 && row<3; i++) {

            if (i == insertionFilter.size()) {
                i = 0;
                scrollPage = 0;
            }
            addFilteredInsertions(i, row,0);
            row++;
        }

        row = 0;

        for (int i = scrollPage; i < scrollPage + 3 && row < 6; i++) {

            if(i == insertionFilter.size()) {
                i = 0;
                scrollPage = 0;
            }
            addFilteredInsertions(i+3, row,5);
            row++;
        }

        scrollPage+=6;
        insertionFind.setCenter(insertionList);

        prevSearch.setDisable(false);
        prevSearch.setVisible(true);
    }
}
