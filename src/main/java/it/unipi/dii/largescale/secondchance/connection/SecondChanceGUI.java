package main.java.it.unipi.dii.largescale.secondchance.connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionMongoDB;
import main.java.it.unipi.dii.largescale.secondchance.connection.ConnectionNeo4jDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class SecondChanceGUI extends Application {

    public static void main(String[] args) {

        ConnectionMongoDB.connMongo = new ConnectionMongoDB();
        ConnectionMongoDB.connMongo.openConnection();
        ConnectionNeo4jDB.connNeo = new ConnectionNeo4jDB();
        launch(args);
        ConnectionMongoDB.connMongo.closeConnection();
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
