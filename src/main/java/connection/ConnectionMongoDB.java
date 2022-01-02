package main.java.connection;

import com.mongodb.client.*;
import main.java.controller.SignInController;
import main.java.entity.Insertion;
import main.java.entity.User;
import org.bson.Document;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.ConnectionString;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class ConnectionMongoDB{

    private MongoClient mongoClient;
    private MongoDatabase db;

    public void openConnection(){
        ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
        mongoClient = MongoClients.create(uri);
        db = mongoClient.getDatabase("project");
    }

    public void closeConnection() {
        mongoClient.close();
    }

    public boolean logInUser(String username, String password) throws IOException {

        this.openConnection();
        MongoCollection<Document> myColl = db.getCollection("user");
        MongoCursor<Document> cursor  = myColl.find(and(eq("username", username),
                eq("password", password))).iterator();
        if(!cursor.hasNext()) {
            System.out.println("Username or Password wrong, try again");
            this.closeConnection();
            return false;
        } else{
            System.out.println("FOUND!!!!");
        }
        this.closeConnection();
        return true;
    }

    public User findUserDetails(String username)
    {
        this.openConnection();
        User logUser = new User();
        MongoCollection<Document> myColl = db.getCollection("user");
        Document user = myColl.find(eq("username", username)).first();
        logUser.setUsername(user.getString("username"));
        logUser.setEmail(user.getString("email"));
        logUser.setAddress(user.getString("address"));
        logUser.setCity(user.getString("city"));
        logUser.setCountry(user.getString("country"));
        this.closeConnection();
        return logUser;
    }

    public void followedUserinsertions() {
    }

    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson());
    }

    public ArrayList<Document> findViralInsertions(int k) {

        this.openConnection();
        ArrayList<Document> array = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("insertion");
        Bson sort = sort(descending("interested"));
        Bson project = project(fields(excludeId(), include("seller"), include("image_url"), include("title"), include("interested"), include("price")));
        Bson limit = limit(k);
        myColl.aggregate(Arrays.asList(sort,project ,limit)).forEach(printDocuments());
        AggregateIterable<Document> r = myColl.aggregate(Arrays.asList(sort,project ,limit));

        for (Document document : r) {
            array.add(document);
        }
        this.closeConnection();
        return array;
    }
}