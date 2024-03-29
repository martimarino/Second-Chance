package main.java.it.unipi.dii.largescale.secondchance.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.it.unipi.dii.largescale.secondchance.cellStyle.CustomCellRank;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;
import main.java.it.unipi.dii.largescale.secondchance.entity.Insertion;
import main.java.it.unipi.dii.largescale.secondchance.utils.Utility;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class StatsController {

    private final String[] countries = new String[]{"Italy", "Canada", "Spain", "Austria", "Germany", "France", "Brazil", "Netherlands", "Poland", "Ireland", "United Kingdom (Great Britain)"};
    private final String[] categories = new String[]{"clothing", "accessories", "bags", "beauty", "house", "jewelry", "kids", "shoes"};

    @FXML
    private TextField boxKNumber;
    @FXML
    private TextField txtFieldCountry;
    @FXML
    private TextField txtFieldCategory;

    @FXML
    private RadioButton rBTopKRated;
    @FXML
    private RadioButton rBPurch;
    @FXML
    private RadioButton rBSold;
    @FXML
    private RadioButton rBTopKInterestingIns;
    @FXML
    private RadioButton rBTopKViewedIns;
    @FXML
    public RadioButton rBLikesPerCategory;

    @FXML
    private Button elaboraButton;

    ConnectionNeo4jDB connNeo;

    public void initialize() {

        connNeo = new ConnectionNeo4jDB();

        Tooltip countries = new Tooltip("Italy, Canada, Spain, Austria, Germany, France, Brazil, Netherlands, Poland, Ireland, United Kingdom");
        Tooltip categories = new Tooltip("clothing, accessories, bags, beauty, house, jewelry, kids, shoes");

        elaboraButton.setDisable(true);
        boxKNumber.setEditable(false);
        boxKNumber.setMouseTransparent(true);
        txtFieldCountry.setEditable(false);
        txtFieldCountry.setMouseTransparent(true);
        txtFieldCategory.setEditable(false);
        txtFieldCategory.setMouseTransparent(true);

        txtFieldCountry.setTooltip(countries);
        txtFieldCategory.setTooltip(categories);

        listenerTopKRated();

        listenerUserPurch();

        listenerUserSold();

        listenerTopKViewed();

        listenerTopKInterested();

        listenerNumberOfLikes();

    }

    private void listenerTopKRated() {

        rBTopKRated.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            boxKNumber.setEditable(true);
            boxKNumber.setMouseTransparent(false);
            txtFieldCountry.setText("");
            txtFieldCountry.setEditable(true);
            txtFieldCountry.setMouseTransparent(false);

            elaboraButton.setDisable(false);

        });

    }

    private void listenerUserPurch() {

        rBPurch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            boxKNumber.setEditable(true);
            boxKNumber.setMouseTransparent(false);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(false);
            txtFieldCategory.setMouseTransparent(true);

            elaboraButton.setDisable(false);
        });
    }

    private void listenerUserSold(){
        rBSold.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            boxKNumber.setEditable(true);
            boxKNumber.setMouseTransparent(false);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(false);
            txtFieldCategory.setMouseTransparent(true);

            elaboraButton.setDisable(false);

        });

    }

    private void listenerTopKViewed(){
        rBTopKViewedIns.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            boxKNumber.setEditable(true);
            boxKNumber.setMouseTransparent(false);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(true);
            txtFieldCategory.setMouseTransparent(false);

            elaboraButton.setDisable(false);

        });

    }

    private void listenerTopKInterested() {
        rBTopKInterestingIns.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            boxKNumber.setEditable(true);
            boxKNumber.setMouseTransparent(false);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(true);
            txtFieldCategory.setMouseTransparent(false);

            elaboraButton.setDisable(false);

        });
    }

    private void listenerNumberOfLikes() {

        rBLikesPerCategory.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("radio button changed from " + oldValue + " to " + newValue);

            txtFieldCountry.setText("");
            txtFieldCategory.setText("");

            boxKNumber.setEditable(false);
            boxKNumber.setMouseTransparent(true);

            txtFieldCountry.setEditable(false);
            txtFieldCountry.setMouseTransparent(true);

            txtFieldCategory.setEditable(false);
            txtFieldCategory.setMouseTransparent(true);

            elaboraButton.setDisable(false);

        });
    }

    public void redirectToStatFunction() throws IOException {

        int k;
        // Section Most

        if (rBLikesPerCategory.isSelected()) {
            showNumberInterestingForCategory();
            return;
        }

        if (boxKNumber.getText().equals("") ||
                Integer.parseInt(boxKNumber.getText()) <= 0)
        {
            Utility.infoBox("Please insert a valid K number", "Error", "Empty box!");
            return;
        }

        k = Integer.parseInt(boxKNumber.getText());

        if (rBSold.isSelected())
            showMostActiveUsers(false, k);

        if (rBPurch.isSelected())
            showMostActiveUsers(true, k);

        // Section K

        if (rBTopKRated.isSelected())
            showTopKRatedUser(k);

        if (rBTopKInterestingIns.isSelected())
            showTopKInterestingInsertion(k);

        if (rBTopKViewedIns.isSelected())
            showTopKViewedInsertion(k);

    }

    public void showMostActiveUsers(boolean choice, int k) throws IOException{

        ArrayList<Document> array = ConnectionMongoDB.connMongo.findMostActiveUsers(k, choice);
        StackPane secondaryLayout = new StackPane();

        ListView<CustomCellRank> leaderBoard = new ListView<>();
        ObservableList<CustomCellRank> items = FXCollections.observableArrayList();

        String type = (choice) ? "Purchased Orders" : "Sold Orders";

        for (Document document : array)
            items.add(new CustomCellRank(document.getString("username"), document.getInteger("count")));


        leaderBoard.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 800, 600);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Top " + k + " " + type);
            newWindow.getIcons().add(image);
            secondaryLayout.getChildren().add(leaderBoard);
            newWindow.setScene(secondScene);

            newWindow.show();
        }

    }

    private void showNumberInterestingForCategory() {

        ArrayList<String> array = connNeo.findNumberInterestingForCategory();
        StackPane secondaryLayout = new StackPane();

        ListView<CustomCellRank> leaderBoard = new ListView<>();
        ObservableList<CustomCellRank> items = FXCollections.observableArrayList();

        String[] parts;

        for (String s : array) {

            parts = s.split(":");
            items.add(new CustomCellRank(parts[0], Integer.parseInt(parts[1])));
        }

        leaderBoard.setItems(items);

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            Scene secondScene = new Scene(secondaryLayout, 800, 600);

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

    public void showTopKRatedUser(int k) {

        ArrayList<Document> array;
        String country = txtFieldCountry.getText();

        if (!Arrays.asList(countries).contains(country)) {
            Utility.infoBox("There are no users of this country!", "Error", "Country not found!");
            txtFieldCountry.setText("");
            return;
        }

        array = ConnectionMongoDB.connMongo.findTopKRatedUser(k, country);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(country);

        for (int i=0; i < k; i++) {
            series1.getData().add(new XYChart.Data(array.get(i).getString("username"), array.get(i).getDouble("rating")));
        }


        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Stage stage = new Stage();
            Image image = new Image(imageStream);
            stage.getIcons().add(image);
            stage.setTitle("Stats");
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String,Number> bc =
                    new BarChart<>(xAxis,yAxis);
            bc.setTitle("Top K Rated Users");
            xAxis.setLabel("Username");
            yAxis.setLabel("Rating");
            bc.getData().addAll(series1);
            Scene scene = new Scene(bc,1200,800);
            stage.setScene(scene);
            stage.show();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void showInsertionPage(String uniq_id) {
        Insertion insertion = ConnectionMongoDB.connMongo.findInsertion(uniq_id);

        if(insertion == null) {
            Utility.infoBox("Product already purchased", "Purchased", "Already purchased");
            return;
        }

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {

            Image image = new Image(imageStream);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SearchInsertionController.class.getResource("/FXML/Insertion.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.getIcons().add(image);
            stage.setTitle("Insertion details");
            stage.setScene(new Scene(loader.load()));
            InsertionController controller = loader.getController();
            controller.initialize(insertion, true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupLinks(XYChart.Series<String, Integer> series, ArrayList<String> ids) {
        int i = 0;
        for (final XYChart.Data<String, Integer> dt : series.getData()) {
            final Node n = dt.getNode();
            String id = ids.get(i);
            n.setOnMouseClicked(event ->  {
                showInsertionPage(id);
            });
            i++;
        }
    }

    public void showTopKInterestingInsertion(int k) {

        String category = txtFieldCategory.getText();
        ArrayList<Document> array;
        array = ConnectionMongoDB.connMongo.findTopKInterestingInsertion(k, category);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(category);

        if(!Arrays.asList(categories).contains(category)) {
            Utility.infoBox("Please insert a valid category", "Error", "Category not found!");
            return;
        }

        ArrayList<String> ids = new ArrayList<>();
        for (int i=0; i < k; i++) {
            series1.getData().add(new XYChart.Data(array.get(i).get("_id").toString(), array.get(i).getInteger("interested")));
            ids.add(array.get(i).get("_id").toString());
        }


        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Stage stage = new Stage();
            Image image = new Image(imageStream);
            stage.getIcons().add(image);
            stage.setTitle("Stats");
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String,Number> bc =
                    new BarChart<>(xAxis,yAxis);
            bc.setTitle("Top K Viewed Insertions");
            xAxis.setLabel("Insertion ID");
            yAxis.setLabel("Number of interested users");
            bc.getData().addAll(series1);
            setupLinks(series1, ids);
            Scene scene = new Scene(bc,1200,800);
            stage.setScene(scene);
            stage.show();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void showTopKViewedInsertion(int k){

        ArrayList<Document> array;

        String category = txtFieldCategory.getText();

        if(!Arrays.asList(categories).contains(category)) {
            Utility.infoBox("Please insert a valid category", "Error", "Category not found!");
            return;
        }

        array = ConnectionMongoDB.connMongo.findTopKViewedInsertion(k, category);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(category);
        for (int i=0; i < k; i++) {
            series1.getData().add(new XYChart.Data(array.get(i).get("_id").toString(), array.get(i).getInteger("views")));
        }
        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Stage stage = new Stage();
            Image image = new Image(imageStream);
            stage.getIcons().add(image);
            stage.setTitle("Stats");
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            final BarChart<String,Number> bc =
                    new BarChart<>(xAxis,yAxis);
            bc.setTitle("Top K Viewed Insertions");
            xAxis.setLabel("Insertion ID");
            yAxis.setLabel("Number of views");
            Scene scene  = new Scene(bc,1200,800);
            bc.getData().addAll(series1);
            stage.setScene(scene);
            stage.show();
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }
}
