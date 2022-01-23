package main.java.GUI;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;

import java.io.*;
import java.net.*;

public class SecondChanceGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws IOException {

        try( FileInputStream imageStream = new FileInputStream("target/classes/img/secondchance.png") ) {
            Image image = new Image(imageStream);
            URL url = new File("src/main/resources/FXML/SignIn.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            primaryStage.getIcons().add(image);
            primaryStage.setTitle("SecondChance");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        }

    }

}
