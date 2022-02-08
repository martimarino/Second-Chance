package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.cellStyle.CustomCellRank;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class StatsController {

    private final String[] countries = new String[]{"Italy", "Canada", "Spain", "Austria", "Germany", "France", "Brazil", "Netherlands", "Poland", "Ireland", "United Kingdom (Great Britain)"};
    private final String[] categories = new String[]{"clothing","accessories", "bags","beauty", "house", "jewelry", "kids", "shoes"};
    public RadioButton followedUsers;
    public RadioButton categoryInsertion;
    public RadioButton postedCountry;

    @FXML private RadioButton rBUsers;
    @FXML private RadioButton rBSellers;
    @FXML private RadioButton rBTopKUsers;
    @FXML private RadioButton rBTopKInterestingIns;
    @FXML private RadioButton rBTopKViewedIns;

    @FXML private TextField boxKNumber;
    @FXML private TextField txtFieldCountry;
    @FXML private TextField txtFieldCategory;

    @FXML private Button elaboraButton;

    ConnectionNeo4jDB connNeo;

    public void initialize(){

        connNeo = new ConnectionNeo4jDB();

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

               if (Objects.equals(txtFieldCategory.getText(), ""))
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

            if (Objects.equals(txtFieldCategory.getText(), ""))
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

        followedUsers.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    elaboraButton.setDisable(false);

        });

        categoryInsertion.selectedProperty().addListener((observable, oldValue, newValue) -> {
                elaboraButton.setDisable(false);

        });

        postedCountry.selectedProperty().addListener((observable, oldValue, newValue) -> {
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

        if (categoryInsertion.isSelected()) {
            showNumberInterestingForCategory();
            return;
        }
        if (postedCountry.isSelected()) {
            showNumberPostedForCountry();
            return;
        }

        if (Objects.equals(boxKNumber.getText(), "")) {
            Utility.infoBox("Please insert a valid K number", "Error", "Empty box!");
            return;
        }

        k = Integer.parseInt(boxKNumber.getText());

        if (rBSellers.isSelected())
            showMostActiveUsersSellers(false, k);

        if (rBUsers.isSelected())
            showMostActiveUsersSellers(true, k);
        if(followedUsers.isSelected())
            showMostFollowedUsers(k);

        // Section K

        if (rBTopKUsers.isSelected())
            showTopKRatedUser(k);

        if (rBTopKInterestingIns.isSelected())
            showTopKInterestingInsertion(k);

        if (rBTopKViewedIns.isSelected())
            showTopKViewedInsertion(k);

    }

    public void showMostActiveUsersSellers(boolean choice, int k) throws IOException{

        ArrayList<Document> array = ConnectionMongoDB.connMongo.findMostActiveUsersSellers(k, choice);
        StackPane secondaryLayout = new StackPane();

        ListView<CustomCellRank> leaderBoard = new ListView<CustomCellRank>();
        ObservableList<CustomCellRank> items = FXCollections.observableArrayList();

        String type = (choice) ? "Purchased Orders" : "Sold Orders";

        for (int i = 0; i < array.size(); i++) {

            items.add(new CustomCellRank(array.get(i).getString("username"), array.get(i).getInteger("count")));
        }

        leaderBoard.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 1200, 800);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Top " + k + " " + type);
            newWindow.getIcons().add(image);
            secondaryLayout.getChildren().add(leaderBoard);
            newWindow.setScene(secondScene);

            newWindow.show();
        }

    }

    private void showNumberPostedForCountry() {

        ArrayList<String> array = connNeo.findNumberPostedInsertionForCountry();
        StackPane secondaryLayout = new StackPane();

        ListView<CustomCellRank> leaderBoard = new ListView<CustomCellRank>();
        ObservableList<CustomCellRank> items = FXCollections.observableArrayList();

        String[] parts;

        for (int i = 0; i < array.size(); i++) {

            parts = array.get(i).split(":");
            items.add(new CustomCellRank(parts[0], Integer.parseInt(parts[1])));
        }

        leaderBoard.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 1200, 800);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Show Number of Post by Country");
            newWindow.getIcons().add(image);
            secondaryLayout.getChildren().add(leaderBoard);
            newWindow.setScene(secondScene);

            newWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNumberInterestingForCategory() {

        ArrayList<String> array = connNeo.findNumberInterestingForCategory();
        StackPane secondaryLayout = new StackPane();

        ListView<CustomCellRank> leaderBoard = new ListView<CustomCellRank>();
        ObservableList<CustomCellRank> items = FXCollections.observableArrayList();

        String[] parts;

        for (int i = 0; i < array.size(); i++) {

            parts = array.get(i).split(":");
            items.add(new CustomCellRank(parts[0], Integer.parseInt(parts[1])));
        }

        leaderBoard.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 1200, 800);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Show Number of Interesting by Category");
            newWindow.getIcons().add(image);
            secondaryLayout.getChildren().add(leaderBoard);
            newWindow.setScene(secondScene);

            newWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMostFollowedUsers(int k) {

        ArrayList<String> array = connNeo.findMostFollowedUsers(k);
        StackPane secondaryLayout = new StackPane();

        ListView<CustomCellRank> leaderBoard = new ListView<CustomCellRank>();
        ObservableList<CustomCellRank> items = FXCollections.observableArrayList();

        String[] parts;

        for (int i = 0; i < array.size(); i++) {

            parts = array.get(i).split(":");
            items.add(new CustomCellRank(parts[0], Integer.parseInt(parts[1])));
        }

        leaderBoard.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 1200, 800);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Show Most Followed Users");
            newWindow.getIcons().add(image);
            secondaryLayout.getChildren().add(leaderBoard);
            newWindow.setScene(secondScene);

            newWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTopKRatedUser(int k) {

        ArrayList<Document> array;
        String country = txtFieldCountry.getText();

        if (!Arrays.asList(countries).contains(country)) {
            Utility.infoBox("Please insert a valid country", "Error", "Country not found!");
            txtFieldCountry.setText("");
            return;
        }

        array = ConnectionMongoDB.connMongo.findTopRatedUsersByCountry(country);
        int arrayRatings[] = new int[6];

        for (int i = 0; i < array.size() - 1; i++) {

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

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png")) {
            Image image = new Image(imageStream);
            Scene scene = new Scene(new Group());
            // New window (Stage)
            ((Group) scene.getRoot()).getChildren().add(chart);
            Stage newWindow = new Stage();
            newWindow.getIcons().add(image);
            newWindow.setTitle("Top " + k);
            newWindow.setScene(scene);
            newWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTopKInterestingInsertion(int k) {

        String category = txtFieldCategory.getText();
        ArrayList<Document> array;
        array = ConnectionMongoDB.connMongo.findTopKViewedInsertion(k, category);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(category);

        if(!Arrays.asList(categories).contains(category))
            Utility.infoBox("Please insert a valid category", "Error", "Category not found!");

        for (int i=0; i < k; i++) {
            series1.getData().add(new XYChart.Data(array.get(i).getObjectId("_id").toString(), array.get(i).getInteger("interested")));
        }


        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Stage stage = new Stage();
            Image image = new Image(imageStream);
            stage.getIcons().add(image);
            stage.setTitle("Stats");
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String,Number> bc =
                    new BarChart<String,Number>(xAxis,yAxis);
            bc.setTitle("Top K Viewed Insertions");
            xAxis.setLabel("Insertion ID");
            yAxis.setLabel("Number of interested users");
            bc.getData().addAll(series1);
            Scene scene = new Scene(bc,1200,800);
            stage.setScene(scene);
            stage.show();
        }


        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void showTopKViewedInsertion(int k){

        ArrayList<Document> array;

        String category = txtFieldCategory.getText();

        if(!Arrays.asList(categories).contains(category))
            Utility.infoBox("Please insert a valid category", "Error", "Category not found!");

        array = ConnectionMongoDB.connMongo.findTopKViewedInsertion(k, category);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(category);
        for (int i=0; i < k; i++) {
            series1.getData().add(new XYChart.Data(array.get(i).getObjectId("_id").toString(), array.get(i).getInteger("views")));
        }

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Stage stage = new Stage();
            Image image = new Image(imageStream);
            stage.getIcons().add(image);
            stage.setTitle("Stats");
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String,Number> bc =
                    new BarChart<String,Number>(xAxis,yAxis);
            bc.setTitle("Top K Viewed Insertions");
            xAxis.setLabel("Insertion ID");
            yAxis.setLabel("Number of views");
            Scene scene  = new Scene(bc,1200,800);
            bc.getData().addAll(series1);
            stage.setScene(scene);
            stage.show();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
