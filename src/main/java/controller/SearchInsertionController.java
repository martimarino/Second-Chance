package main.java.controller;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import main.java.connection.*;
import main.java.utils.*;
import org.bson.*;

import java.util.ArrayList;

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
    public int item, scrollPage;
    public Button prevSearch, nextSearch;

    public void initialize(){

        insertionList = new GridPane();
        item = 0;

        prevSearch.setDisable(true);
        nextSearch.setDisable(true);
        prevSearch.setVisible(false);
        nextSearch.setVisible(false);

    }

    public void findInsertion(MouseEvent mouseEvent) {

        ConnectionMongoDB conn = new ConnectionMongoDB();

        item = 0;
        if(ins.getText().equals("")) {  //filters case

            if(!(size.getValue().equals("size") && price.getValue().equals("price")
                && gender.getValue().equals("gender") && status.getValue().equals("status")
                && category.getValue().equals("category") && color.getValue().equals("color"))) {

                //take combobox value and search
                insertionFilter = conn.findInsertionByFilters(size.getValue(), price.getValue(), gender.getValue(), status.getValue(), category.getValue(), color.getValue());
                if(insertionFilter.isEmpty())
                {
                    Utility.infoBox("There is not an insertion with this characteristics!", "Advise", "User Advise");
                    return;
                }

                if(insertionFilter.size() > 1)
                {
                    nextSearch.setDisable(false);
                    nextSearch.setVisible(true);
                }
                showFilteredInsertions();
                item++;
                insertionFind.setCenter(insertionList);
                
            }
        } else {        //search case

            //search insertion by seller
            insertionFilter = conn.findInsertionBySeller(ins.getText());

            if(insertionFilter.isEmpty()){   //if no article is found try to search for brands
                //search by brand
                insertionFilter = conn.findInsertionByBrand(ins.getText());
            }

            if(insertionFilter.isEmpty())
            {
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

        Label seller = new Label("Seller: " + insertionFilter.get(index).getString("seller"));
        ImageView image = new ImageView(insertionFilter.get(index).getString("image_url"));
        image.setFitHeight(150);
        image.setFitWidth(150);
        Label status = new Label("Status: " + insertionFilter.get(index).getString("status"));
        Label price = new Label(insertionFilter.get(index).getDouble("price") + "€");
        Label brand = new Label("Brand: " + insertionFilter.get(index).getString("brand"));

        insertionList.add(seller, i, j);
        insertionList.add(image, i, j+1);
        insertionList.add(status, i, j+2);
        insertionList.add(price, i, j+3);
        insertionList.add(brand, i, j+4);

        GridPane.setHalignment(seller, HPos.CENTER);
        GridPane.setHalignment(image, HPos.CENTER);
        GridPane.setHalignment(status, HPos.CENTER);
        GridPane.setHalignment(price, HPos.CENTER);
        GridPane.setHalignment(brand, HPos.CENTER);

        insertionList.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }

    public void PrevFilteredInsertion(MouseEvent mouseEvent) {

        insertionList.getChildren().clear();
        int row = 0;
        if(scrollPage == 6) {
            prevSearch.setDisable(true);
            prevSearch.setVisible(false);
        }
        if(scrollPage == 0) {
            scrollPage = insertionFilter.size() - 6;
        }
        else
            scrollPage-=6;

        for(int i = scrollPage; row<3; i++)
        {
            addFilteredInsertions(i, row, 0);
            row++;
        }
        row = 0;
        for(int i = scrollPage; row<3; i++)
        {
            addFilteredInsertions(i+3, row, 5);
            row++;
        }
        insertionFind.setCenter(insertionList);

    }

    public void NextFilteredInsertion(MouseEvent mouseEvent) {

        insertionList.getChildren().clear();
        int row = 0;

        for(int i = scrollPage; i < scrollPage+3 && row<3; i++)
        {
            if(i == insertionFilter.size()) {
                i = 0;
                scrollPage = 0;
            }
            addFilteredInsertions(i, row,0);
            row++;
        }
        row = 0;
        for(int i = scrollPage; i < scrollPage+3 && row<6; i++)
        {
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
