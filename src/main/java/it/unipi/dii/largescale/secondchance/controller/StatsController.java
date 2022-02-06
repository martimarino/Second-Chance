package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class StatsController {

    private final String[] countries = new String[]{"Italy", "Canada", "Spain", "Austria", "Germany", "France", "Brazil", "Netherlands", "Poland", "Ireland", "United Kingdom (Great Britain)"};
    private final String[] categories = new String[]{"clothing","accessories", "bags","beauty", "house", "jewelry", "kids", "shoes"};

    @FXML private RadioButton rBUsers;
    @FXML private RadioButton rBSellers;
    @FXML private RadioButton rBTopKUsers;
    @FXML private RadioButton rBTopKInterestingIns;
    @FXML private RadioButton rBTopKViewedIns;

    @FXML private TextField boxKNumber;
    @FXML private TextField txtFieldCountry;
    @FXML private TextField txtFieldCategory;

    @FXML private Button elaboraButton;

    public void initialize(){

        Tooltip countries = new Tooltip("Italy, Canada, Spain, Austria, Germany, France, Brazil, Netherlands, Poland, Ireland, United Kingdom");
        Tooltip categories = new Tooltip("clothing,accessories, bags, beauty, house, jewelry, kids, shoes");

        elaboraButton.setDisable(true);
        txtFieldCountry.setEditable(false);
        txtFieldCountry.setMouseTransparent(true);
        txtFieldCategory.setEditable(false);
        txtFieldCategory.setMouseTransparent(true);

        txtFieldCountry.setTooltip(countries);
        txtFieldCategory.setTooltip(categories);

        rBTopKViewedIns.selectedProperty().addListener((observable, oldValue, newValue) -> {
               System.out.println("radio button changed from " + oldValue + " to " + newValue);

               txtFieldCountry.setText("");
               txtFieldCountry.setEditable(false);
               txtFieldCountry.setMouseTransparent(true);

               elaboraButton.setDisable(true);

               if (!txtFieldCategory.isEditable()) {
                   txtFieldCategory.setEditable(true);
                   txtFieldCategory.setMouseTransparent(false);
               }
        });

       rBTopKInterestingIns.selectedProperty().addListener((observable, oldValue, newValue) -> {
           System.out.println("radio button changed from " + oldValue + " to " + newValue);

           txtFieldCountry.setText("");
           txtFieldCountry.setEditable(false);
           txtFieldCountry.setMouseTransparent(true);

           elaboraButton.setDisable(true);

           if (!txtFieldCategory.isEditable()) {
               txtFieldCategory.setEditable(true);
               txtFieldCategory.setMouseTransparent(false);
           }
       });

        rBTopKUsers.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            txtFieldCategory.setText("");
            txtFieldCategory.setEditable(false);
            txtFieldCategory.setMouseTransparent(true);

            elaboraButton.setDisable(true);

            if (txtFieldCategory.isEditable()) {
                txtFieldCategory.setEditable(false);
                txtFieldCategory.setMouseTransparent(true);
            }

            txtFieldCountry.setEditable(true);
            txtFieldCountry.setMouseTransparent(false);

        });

        rBSellers.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(false);
            txtFieldCategory.setMouseTransparent(true);

            elaboraButton.setDisable(false);
        });

        rBUsers.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(false);
            txtFieldCategory.setMouseTransparent(true);

            elaboraButton.setDisable(false);
        });

        txtFieldCategory.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text changed from " + oldValue + " to " + newValue);

            elaboraButton.setDisable(Objects.equals(newValue, ""));
        });

        txtFieldCountry.textProperty().addListener((observable, oldValue, newValue) -> {
            Utility.printTerminal("text changed from " + oldValue + " to " + newValue);

            elaboraButton.setDisable(Objects.equals(newValue, ""));
        });
    }

    public void redirectToStatFunction() throws IOException {

        int k;
        // Section Most

        if (Objects.equals(boxKNumber.getText(), "")) {
            Utility.infoBox("Please insert a valid K number", "Error", "Empty box!");
            return;
        }

        k = Integer.parseInt(boxKNumber.getText());

        if (rBSellers.isSelected())
            showMostActiveUsersSellers(ConnectionMongoDB.connMongo,false, k);

        if (rBUsers.isSelected())
            showMostActiveUsersSellers(ConnectionMongoDB.connMongo, true, k);

        // Section K

        if (rBTopKUsers.isSelected())
            showTopKRatedUser(ConnectionMongoDB.connMongo, k);

        if (rBTopKInterestingIns.isSelected())
            showTopKInterestingInsertion(ConnectionMongoDB.connMongo, k);

        if (rBTopKViewedIns.isSelected())
            showTopKViewedInsertion(ConnectionMongoDB.connMongo, k);

    }

    public void showMostActiveUsersSellers(ConnectionMongoDB conn, boolean choice, int k) throws IOException{

        ArrayList<Document> array = conn.findMostActiveUsersSellers(k, choice);
        StackPane secondaryLayout = new StackPane();

        ListView<String> list = new ListView<>();
        ObservableList items = FXCollections.observableArrayList();

        for (int i = 0; i < array.size(); i++) {

            String str = array.get(i).getString("username") + ":    " + array.get(i).getInteger("count").toString();
            items.add(str);
        }

        list.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 920, 400);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Top " + k);
            newWindow.getIcons().add(image);
            secondaryLayout.getChildren().add(list);
            newWindow.setScene(secondScene);

            newWindow.show();
        }
    }

    public void showTopKRatedUser(ConnectionMongoDB conn, int k) throws IOException {

        ArrayList<Document> array;
        String country = txtFieldCountry.getText();

        if(!Arrays.asList(countries).contains(country)) {
            Utility.infoBox("Please insert a valid country", "Error", "Country not found!");
            txtFieldCountry.setText("");
            return;
        }

        array = conn.findTopRatedUsersByCountry(country);
        int arrayRatings[] = new int[6];

        for (int i = 0; i < array.size()-1; i++) {

            int rating = array.get(i).getDouble("rating").intValue();
            arrayRatings[rating]++;
        }

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Rating 5", arrayRatings[5]),
                        new PieChart.Data("Rating 4", arrayRatings[4]),
                        new PieChart.Data("Rating 3", arrayRatings[3]),
                        new PieChart.Data("Rating 2", arrayRatings[2]),
                        new PieChart.Data("Rating 1", arrayRatings[1]));

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Top K Rated User in " + country);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene scene = new Scene(new Group());
            // New window (Stage)
            ((Group) scene.getRoot()).getChildren().add(chart);
            Stage newWindow = new Stage();
            newWindow.getIcons().add(image);
            newWindow.setTitle("Top " + k);
            newWindow.setScene(scene);
            newWindow.show();
        }
    }

    public void showTopKInterestingInsertion(ConnectionMongoDB conn, int k) throws IOException {

        ArrayList<Document> array;
        String category = txtFieldCategory.getText();

        if(!Arrays.asList(categories).contains(category)) {
            Utility.infoBox("Please insert a valid category", "Error", "Category not found!");
            return;
        }

        array = conn.findTopKInterestingInsertion(k, category);

        StackPane secondaryLayout = new StackPane();

        ListView<String> list = new ListView<String>();
        ObservableList items = FXCollections.observableArrayList();

        for (int i=0; i < k; i++) {

            String str = array.get(i).getString("description") + ": " + array.get(i).getInteger("interested").toString();
            items.add(str);
        }

        list.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 1200, 800);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.getIcons().add(image);
            newWindow.setTitle("Top " + k);
            secondaryLayout.getChildren().add(list);
            newWindow.setScene(secondScene);

            newWindow.show();
        }
    }

    public void showTopKViewedInsertion(ConnectionMongoDB conn, int k) throws IOException {

        ArrayList<Document> array;

        String category = txtFieldCategory.getText();

        if(!Arrays.asList(categories).contains(category))
            Utility.infoBox("Please insert a valid category", "Error", "Category not found!");

        array = conn.findTopKViewedInsertion(k, category);

        StackPane secondaryLayout = new StackPane();

        ListView<String> list = new ListView<String>();
        ObservableList items = FXCollections.observableArrayList();

        for (int i=0; i < k; i++) {

            String str = array.get(i).getString("description") + ": " + array.get(i).getInteger("views").toString();
            items.add(str);
        }

        list.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 1200, 800);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.getIcons().add(image);
            newWindow.setTitle("Top " + k);
            secondaryLayout.getChildren().add(list);
            newWindow.setScene(secondScene);

            newWindow.show();
        }
    }
}
