package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class SecondChanceGUI extends Application {

    public static void main(String[] args) {
/*
        ConnectionString uri = new ConnectionString(
                "mongodb+srv://root:<password>@largescaleproject.n8fgw.mongodb.net/test");
        MongoClient mongoClientAtlas = MongoClients.create(uri);

        MongoDatabase db = mongoClientAtlas.getDatabase("LargeScaleProject");

        for(String name : db.listCollectionNames()) {

            Utility.printTerminal(name);
        }
*/

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
