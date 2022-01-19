package main.java.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.stage.Stage;
import org.bson.*;



public class Utility {

    public static void infoBox(String infoMessage, String titleBar, String headerMessage)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }


    public static void showUsers(GridPane list, ArrayList<Document> filter, int item) throws IOException {

        list.getChildren().clear();

        try (FileInputStream imageStream = new FileInputStream("target/classes/img/user.png")) {
           Image image = new Image(imageStream);

           Label username = new Label(filter.get(item).getString("username"));
           Label country = new Label(filter.get(item).getString("country"));
           Label city = new Label(filter.get(item).getString("city"));

           list.add(new ImageView(image), 0, 0);
           list.add(username, 0, 1);
           list.add(country, 0, 2);
           list.add(city, 0, 3);

           GridPane.setHalignment(username, HPos.CENTER);
           GridPane.setHalignment(country, HPos.CENTER);
           GridPane.setHalignment(city, HPos.CENTER);
        }
        list.setStyle(
                "    -fx-padding: 20;\n" +
                        "    -fx-hgap: 10;\n" +
                        "    -fx-vgap: 10;");

    }

    public static String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 24;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        printTerminal(generatedString);
        return generatedString;
    }

    public static void printTerminal(String msg){
        System.out.println("************************************************");
        System.out.println(msg);
        System.out.println("************************************************");
    }

    public static void changePage(Button element, String page) throws IOException {
        URL url = new File("src/main/resources/FXML/" + page + ".fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage stage = (Stage) element.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

}
