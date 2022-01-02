package main.java.connection;

import com.mongodb.client.*;
import main.java.entity.*;
import main.java.utils.*;
import org.bson.Document;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import com.mongodb.ConnectionString;
import org.bson.conversions.Bson;

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
        db = mongoClient.getDatabase("local");
    }

    public void closeConnection() {
        mongoClient.close();
    }

    public boolean logInUser(String username, String password) throws IOException {

        this.openConnection();
        if(!userAlreadyPresent(username, password)) {
            System.out.println("Username or Password wrong, try again");
            this.closeConnection();
            return false;
        } else{
            System.out.println("FOUND!!!!");
        }
        this.closeConnection();
        return true;
    }

    public boolean registerUser(User u) {

        this.openConnection();

        if(userAlreadyPresent(u.getUsername(), u.getPassword())) {
            Utility.infoBox("Please, choose another username and try again.", "Error", "Username already used!");
            return false;
        }

        MongoCollection<Document> myColl = db.getCollection("user");
        Document user = new Document("address", u.getAddress())
                .append("city", u.getCity())
                .append("country", u.getCountry())
                .append("email", u.getEmail())
                .append("name", u.getName())
                .append("password", u.getPassword())
                .append("username", u.getUsername())
                .append("suspended", u.getSuspended());
        myColl.insertOne(user);
        this.closeConnection();
        return true;
    }

    private boolean userAlreadyPresent(String username, String password) {

        MongoCollection<Document> myColl = db.getCollection("user");
        MongoCursor<Document> cursor  = myColl.find(and(eq("username", username),
                eq("password", password))).iterator();
        if(!cursor.hasNext())
            return false;
        return true;
    }

    public User findUserDetails(String username)
    {
        this.openConnection();
        User logUser = new User();
        MongoCollection<Document> myColl = db.getCollection("user");
        Document user = myColl.find(eq("username", username)).first();
        logUser.setUsername(user.getString("username"));
        logUser.setName(user.getString("name"));
        logUser.setEmail(user.getString("email"));
        logUser.setAddress(user.getString("address"));
        logUser.setCity(user.getString("city"));
        logUser.setCountry(user.getString("country"));
        logUser.setSuspended(user.getString("suspended"));
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